# getRawData.py
# Given some constraints on data via XML, return raw sensor data fitting those constraints

# Python library imports
from xml.etree.ElementTree import fromstring
from xml.etree.ElementTree import ElementTree
from sets import Set

# GAE imports
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db
from google.appengine.runtime.apiproxy_errors import CapabilityDisabledError

# Third party imports
from third_party.geo import geomodel

# Application imports
from lib import senselib
from importer import Measurement

# Enumerate search criteria
class SearchParams:
    idSet = Set()
    startTime = None
    endTime = None
    minLatitude = None
    maxLatitude = None
    minLongitude = None
    maxLongitude = None
    minSpeed = None
    maxSpeed = None
    deviceKindSet = Set()
    deviceIdSet = Set()

# Base functionality for getting data
class GenericDataRetriever(webapp.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        try:
            # Intialize a tree with the input XML
            measXML = self.request.get('xml')
            measurements = fromstring(measXML).getchildren()
            
            # print results for each measurement
            resp = ''
            for m in measurements:
                # Making sure input is a set of measurement tags
                if m.tag != 'measurement':
                    raise ValueError
                    
                elementTree = ElementTree(m)
                innerResp = treeToXML(elementTree)
                
                resp += innerResp
            
        except (CapabilityDisabledError, KeyError, ValueError):
            self.response.out.write("FAILURE")
    
    # Given a tree, return some XML that represents the data
    def treeToXML(self, elemTree):
        try:
            # fill search params if they exist
            sp = SearchParams()
            
            # get all of the IDs to lookup
            ids = elemTree.find('ids')
            if ids:
                idList = ids.findall('id')
                for id in idList:
                    if validateElement(id):
                        sp.idSet.add(id.text)
                        
            # get the minimum/maximum latitude/longtiude
            minLatitude = elemTree.find('minLatitude')
            maxLatitude = elemTree.find('maxLatitude')
            minLongitude = elemTree.find('minLongitude')
            maxLongitude = elemTree.find('maxLongitude')
            # all must exist
            if validateElement(minLatitude) and validateElement(maxLatitude) and \
            validateElement(minLongitude) and validateElement(maxLongitude):
                sp.minLatitude = float(minLatitude.text)
                sp.maxLatitude = float(maxLatitude.text)
                sp.minLongitude = float(minLongitude.text)
                sp.maxLongitude = float(maxLongitude.text)
                        
            # get the minimum/maximum speed
            minSpeed = elemTree.find('minSpeed')
            maxSpeed = elemTree.find('maxSpeed')
            # all must exist
            if validateElement(minSpeed) and validateElement(maxSpeed):
                sp.minSpeed = float(minSpeed.text)
                sp.maxSpeed = float(maxSpeed.text)
            
            # get all of the deviceKinds to lookup
            deviceKinds = elemTree.find('deviceKinds')
            if deviceKinds:
                deviceKindList = deviceKinds.findall('deviceKind')
                for deviceKind in deviceKindList:
                    if validateElement(deviceKind):
                        sp.deviceKindSet.add(deviceKind.text)
            
            # get all of the deviceIds to lookup
            deviceIds = elemTree.find('deviceIds')
            if deviceIds:
                deviceIdList = deviceIds.findall('deviceId')
                for deviceId in deviceIdList:
                    if validateElement(deviceId):
                        sp.deviceIdSet.add(deviceId.text)
                
        except:
            raise ValueError
        return ""
        
    def validateElement(self, elt):
        if elt and elt.text and elt.text != '':
            return True
        else
            return False

application = webapp.WSGIApplication(
                                     [('/getRawData', GenericDataRetriever)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()