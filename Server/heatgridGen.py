# heatgridGen.py
# generate a file for an arbitrary heatgrid

# future imports
from __future__ import with_statement

# Python library imports
from xml.etree.ElementTree import fromstring
from xml.etree.ElementTree import tostring
from xml.etree.ElementTree import ElementTree
from xml.etree.ElementTree import Element
import urllib
import os
import datetime
from datetime import timedelta

# GAE imports
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api import files
from google.appengine.ext import db
from google.appengine.runtime.apiproxy_errors import CapabilityDisabledError

# Application imports
from getRawData import GenericDataRetriever

# limitation: 1 MB maximum
# suggestion: swtich to GAE blobstore and parse with mapreduce once
# both those libraries become more mature
class XMLFile(db.Model):
    xml = db.TextProperty()
    requestXml = db.TextProperty()
    requestX = db.IntegerProperty()
    requestY = db.IntegerProperty()
    timestamp = db.DateTimeProperty()

class HeatgridGenerator(webapp.RequestHandler):
    def post(self):
        try:
            # simply call the actual heatgrid processor with the relevant data
            self.response.headers['Content-Type'] = 'text/plain'
            xml = self.request.get('xml')
            x = self.request.get('xDim')
            y = self.request.get('yDim')
            self.processHeatgrid(xml, x, y)
            self.response.out.write("SUCCESS")
        except:
            self.response.out.write("FAILURE")
    def processHeatgrid(self, measXML, xDim, yDim):
        # convert input XML to raw data
        measXML = measXML.lstrip()
        measInput = fromstring(measXML).getchildren()
        
        xDim = int(xDim)
        yDim = int(yDim)
        
        # get the location bounds
        maxLatitude = 0.0
        maxLongitude = 0.0
        minLatitude = 0.0
        minLongitude = 0.0
        deviceId = '1'
        for m in measInput:
            elementTree = ElementTree(m)
            maxLatitude = float(elementTree.findtext('maxLatitude'))
            maxLongitude = float(elementTree.findtext('maxLongitude'))
            minLatitude = float(elementTree.findtext('minLatitude'))
            minLongitude = float(elementTree.findtext('minLongitude'))
            subelt = elementTree.find('deviceIds')
            if subelt != None and subelt.findtext('deviceId') != None:
                deviceId = subelt.findtext('deviceId')
            
        # figure out the spacing
        deltaLon = (maxLongitude - minLongitude) / float(xDim)
        deltaLat = (maxLatitude - minLatitude) / float(yDim)
        
        # initialize data structure
        weightDict = {}
        numDict = {}
        for i in range(xDim * yDim):
            weightDict[str(i)] = 0.0
            numDict[str(i)] = 0
        
        # get the average data
        gdr = GenericDataRetriever()
        measurements = fromstring(gdr.generateResponse(measXML)).getchildren()
        # first, just get the sum and place it in the appropriate location box
        for m in measurements:
            elementTree = ElementTree(m)
            if elementTree.findtext('latitude') != None and \
               elementTree.findtext('longitude') != None and \
               elementTree.findtext('data') != None:
                data = float(elementTree.findtext('data'))
                lat = float(elementTree.findtext('latitude'))
                lon = float(elementTree.findtext('longitude'))
                yIndex = int((lat - minLatitude) / deltaLat)
                xIndex = int((lon - minLongitude) / deltaLon)
                if xIndex >= 0 and yIndex >= 0 and xIndex < xDim and yIndex < yDim:
                    weightDict[str(yIndex * xDim + xIndex)] += data
                    numDict[str(yIndex * xDim + xIndex)] += 1
                    
        # next, take the average and keep the minimum to normalize to 1.0
        minWeight = 0.0
        for i in range(xDim * yDim):
            if numDict[str(i)] > 0:
                weightDict[str(i)] /= numDict[str(i)]
                if weightDict[str(i)] < minWeight or minWeight < 0.0001:
                    minWeight = weightDict[str(i)]
        
        # normalize weights to 1.0
        for i in range(xDim * yDim):
            if numDict[str(i)] > 0 and minWeight > 0.0:
                weightDict[str(i)] /= minWeight
                
        # set up XML elements
        e = Element('heatgrid')
        
        # add number of rows and columns
        numRows = Element('numRows')
        numRows.text = str(yDim)
        e.append(numRows)
        numColumns = Element('numColumns')
        numColumns.text = str(xDim)
        e.append(numColumns)
        
        # add row/column width/height
        rowHeight = Element('rowHeight')
        rowHeight.text = str(deltaLat)
        e.append(rowHeight)
        columnWidth = Element('columnWidth')
        columnWidth.text = str(deltaLon)
        e.append(columnWidth)
        
        # add origin latitude/longitude
        originLatitude = Element('originLatitude')
        originLatitude.text = str(minLatitude)
        e.append(originLatitude)
        originLongitude = Element('originLongitude')
        originLongitude.text = str(minLongitude)
        e.append(originLongitude)
        
        # add weights
        weights = Element('weights')
        for i in range(xDim * yDim):
            w = Element('w')
            w.text = str(weightDict[str(i)])
            weights.append(w)
        e.append(weights)
        
        # save results to a file
        key = XMLFile(xml=tostring(e), requestXml=measXML, requestX=xDim, requestY=yDim, timestamp=datetime.datetime.now(), key_name=(deviceId))
        key.save()
        return key
            
class HeatgridData(webapp.RequestHandler):
    def get(self):
        try:
            self.response.headers['Content-Type'] = 'text/xml'
            id = '1'
            if self.request.get('id'):
                id = self.request.get('id')
                
            # grab file contents from the datastore cache, and display them
            myFile = XMLFile.get_by_key_name(id)
            if myFile != None:
                # Refresh if the data is stale
                if myFile.timestamp + timedelta(hours=5) < datetime.datetime.now():
                    g = HeatgridGenerator()
                    myFile = g.processHeatgrid(myFile.requestXml, myFile.requestX, myFile.requestY)
                xml = '<?xml version="1.0" encoding="UTF-8"?>\n'
                xml += myFile.xml
                self.response.out.write(xml)
        except:
            pass

application = webapp.WSGIApplication(
                                     [('/heatgridGen', HeatgridGenerator),
                                      ('/heatgrid.xml', HeatgridData),
                                      ('/s/heatgrid.xml', HeatgridData)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()