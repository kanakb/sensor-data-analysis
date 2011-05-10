# heatgridGen.py
# generate a file for an arbitrary heatgrid

# Python library imports
from xml.etree.ElementTree import fromstring
from xml.etree.ElementTree import tostring
from xml.etree.ElementTree import ElementTree
from xml.etree.ElementTree import Element

# GAE imports
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

# Application imports
from getRawData import GenericDataRetriever

class HeatgridGenerator(webapp.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        try:
            # convert input XML to raw data
            measXML = self.request.get('xml')
            measInput = fromstring(measXML).getchildren()
            
            xDim = int(self.request.get('xDim'))
            yDim = int(self.request.get('yDim'))
            
            # get the location bounds
            maxLatitude = 0.0
            maxLongitude = 0.0
            minLatitude = 0.0
            minLongitude = 0.0
            for m in measInput:
                elementTree = ElementTree(m)
                maxLatitude = float(elementTree.findtext('maxLatitude'))
                maxLongtiude = float(elementTree.findtext('maxLongitude'))
                minLatitude = float(elementTree.findtext('minLatitude'))
                minLongitude = float(elementTree.findtext('minLongitude'))
                
            # figure out the spacing
            deltaLon = (maxLongitude - minLongitude) / yDim
            deltaLat = (maxLatitude - minLatitude) / xDim
            
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
                    xIndex = int((lat - minLatitude) / deltaLat)
                    yIndex = int((lat - maxLongitude) / deltaLon)
                    weightDict[str(xIndex * yDim + yIndex)] += data
                    numDict[str(xIndex * yDim + yIndex)] += 1
            # next, take the average and keep the minimum to normalize to 1.0
            minWeight = 0.0
            for i in range(xDim * yDim):
                if numDict[str(i)] > 0:
                    weightDict[str(i)] /= numDict[str(i)]
                    if weightDict[str(i)] < minWeight or minWeight == 0.0:
                        minWeight = weightDict[str(i)]
                        
            # normalize weights to 1.0
            for i in range(xDim * yDim):
                if numDict[str(i)] > 0:
                    weightDict[str(i)] /= minWeight
                
        except (ValueError, TypeError, KeyError):
            self.response.out.write("FAILURE")

application = webapp.WSGIApplication(
                                     [('/heatgridGen', HeatgridGenerator)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()