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
            for i in range(xDim * yDim):
                weightDict[str(i)] = 0.0
            
            # get the average data
            gdr = GenericDataRetriever()
            measurements = fromstring(gdr.generateResponse(measXML)).getchildren()
            for m in measurements:
                elementTree = ElementTree(m)
                data = float(elementTree.findtext('data'))
                lat = float(elementTree.findtext('latitude'))
                lon = float(elementTree.findtext('longitude'))
                xIndex = int((lat - minLatitude) / deltaLat)
                yIndex = int((lat - maxLongitude) / deltaLon)
                weightDict[str(xIndex * yDim + yIndex)] += data
                
        except (CapabilityDisabledError, TypeError, KeyError, ValueError):
            self.response.out.write("FAILURE")

application = webapp.WSGIApplication(
                                     [('/heatgridGen', HeatgridGenerator)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()