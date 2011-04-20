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
from lib.schemas import Measurement

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
                rangeDict = self.treeToDict(elementTree)
                innerResp = self.dictToXML(rangeDict)
                
                resp += str(innerResp) + '\n'
            
            self.response.out.write(resp)
            
        except (CapabilityDisabledError, KeyError, ValueError):
            self.response.out.write("FAILURE")
            
    # Run queries based on the dict to get some XML
    def dictToXML(self, elemDict):
            # start with everything (query isn't run until told to)
            q = Measurement.all()
            
            # add device kinds
            try:
                if elemDict['deviceKindList']:
                    q = q.filter("deviceKind IN", elemDict['deviceKindList'])
            except KeyError:
                pass
            
            # add device IDs
            try:
                if elemDict['deviceIdList']:
                    q = q.filter("deviceId IN", elemDict['deviceIdList'])
            except KeyError:
                pass
                
            # add time range if possible (filter on this first since lots of speeds will be the same)
            timeAdded = True
            try:
                if elemDict['minTime']:
                    q = q.filter("time >", elemDict['minTime']).filter("time <", elemDict['maxTime'])
                else:
                    timeAdded = False
            except KeyError:
                timeAdded = False
                
            # add speed range if time range not specified
            speedAdded = True
            try:
                if not timeAdded and elemDict['minSpeed']:
                    q = q.filter("speed >", elemDict['minSpeed']).filter("speed <", elemDict['maxSpeed'])
                else:
                    speedAdded = False
            except KeyError:
                speedAdded = False
                
    
    # Given a tree, return a dict that summarizes the data
    def treeToDict(self, elemTree):
        try:
            # fill search params if they exist
            sp = {}
                        
            # get the minimum/maximum latitude/longtiude
            minLatitude = elemTree.find('minLatitude')
            maxLatitude = elemTree.find('maxLatitude')
            minLongitude = elemTree.find('minLongitude')
            maxLongitude = elemTree.find('maxLongitude')
            # all must exist
            if self.validateElement(minLatitude) and self.validateElement(maxLatitude) and \
            self.validateElement(minLongitude) and self.validateElement(maxLongitude):
                sp['minLatitude'] = float(minLatitude.text)
                sp['maxLatitude'] = float(maxLatitude.text)
                sp['minLongitude'] = float(minLongitude.text)
                sp['maxLongitude'] = float(maxLongitude.text)
                        
            # get the minimum/maximum time
            minTime = elemTree.find('minTime')
            maxTime = elemTree.find('maxTime')
            # all must exist
            if self.validateElement(minTime) and self.validateElement(maxTime):
                sp['minTime'] = senselib.toDateTime(float(minTime.text))
                sp['maxTime'] = senselib.toDateTime(float(maxTime.text))
                        
            # get the minimum/maximum speed
            minSpeed = elemTree.find('minSpeed')
            maxSpeed = elemTree.find('maxSpeed')
            # all must exist
            if self.validateElement(minSpeed) and self.validateElement(maxSpeed):
                sp['minSpeed'] = float(minSpeed.text)
                sp['maxSpeed'] = float(maxSpeed.text)
            
            # get all of the deviceKinds to lookup
            deviceKinds = elemTree.find('deviceKinds')
            if deviceKinds:
                deviceKindList = deviceKinds.findall('deviceKind')
                sp['deviceKindList'] = Set()
                for deviceKind in deviceKindList:
                    if self.validateElement(deviceKind):
                        sp['deviceKindList'].append(deviceKind.text)
            
            # get all of the deviceIds to lookup
            deviceIds = elemTree.find('deviceIds')
            if deviceIds:
                deviceIdList = deviceIds.findall('deviceId')
                sp['deviceIdList'] = Set()
                for deviceId in deviceIdList:
                    if self.validateElement(deviceId):
                        sp['deviceIdList'].append(deviceId.text)
            
            # the dictionary is set up, so return it
            return sp
            
        except:
            raise ValueError
        
    def validateElement(self, elt):
        if elt != None and elt.text and elt.text != '':
            return True
        else:
            return False

application = webapp.WSGIApplication(
                                     [('/getRawData', GenericDataRetriever)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()