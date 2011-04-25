# getRawData.py
# Given some constraints on data via XML, return raw sensor data fitting those constraints

# Python library imports
from xml.etree.ElementTree import fromstring
from xml.etree.ElementTree import tostring
from xml.etree.ElementTree import ElementTree
from xml.etree.ElementTree import Element
from sets import Set

# GAE imports
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db
from google.appengine.runtime.apiproxy_errors import CapabilityDisabledError

# Third party imports
from third_party.geo import geomodel
from third_party.geo import geotypes

# Application imports
from lib import senselib
from lib.schemas import Measurement
from sensors import sensorChooser
from sensors.genericSensor import DataType

# Base functionality for getting data
class GenericDataRetriever(webapp.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        try:
            # Intialize a tree with the input XML
            measXML = self.request.get('xml')
            measurements = fromstring(measXML).getchildren()
            
            # print results for each measurement
            resp = '<measurements>\n'
            for m in measurements:
                # Making sure input is a set of measurement tags
                if m.tag != 'measurement':
                    raise ValueError
                    
                elementTree = ElementTree(m)
                rangeDict = self.treeToDict(elementTree)
                innerResp = self.dictToXML(rangeDict)
                
                resp += str(innerResp) + '\n'
            
            self.response.out.write(resp + '</measurements>')
        except (CapabilityDisabledError, KeyError, ValueError):
            self.response.out.write("FAILURE")
            
    # Run queries based on the dict to get some XML
    def dictToXML(self, elemDict):
        # start with everything (query isn't run until told to)
        q = Measurement.all()
        
        # add device kinds
        if 'deviceKindList' in elemDict and elemDict['deviceKindList']:
            q = q.filter("deviceKind IN", elemDict['deviceKindList'])
        
        # add device IDs
        if 'deviceIdList' in elemDict and elemDict['deviceIdList']:
            q = q.filter("deviceId IN", elemDict['deviceIdList'])
        
        # add sensor kinds
        if 'sensorKindList' in elemDict and elemDict['sensorKindList']:
            q = q.filter("sensorKind IN", elemDict['sensorKindList'])
            
        # add data list if it exists
        if 'dataList' in elemDict and elemDict['dataList']:
            q = q.filter("stringData IN", elemDict['dataList'])
            
        # add time range if possible (filter on this first since lots of speeds will be the same)
        timeAdded = False
        if 'minTime' in elemDict and elemDict['minTime']:
            q = q.filter("time >", elemDict['minTime']).filter("time <", elemDict['maxTime'])
            timeAdded = True
            
        # add speed range if time range not specified
        speedNeeded = False
        speedAdded = False
        if 'minSpeed' in elemDict and elemDict['minSpeed'] != None:
            speedNeeded = True
            if not timeAdded:
                q = q.filter("speed >=", elemDict['minSpeed']).filter("speed <=", elemDict['maxSpeed'])
                speedAdded = True
            
        # add int data range if time and speed range not specified
        intDataNeeded = False
        intDataAdded = False
        if 'minIntData' in elemDict and elemDict['minIntData'] != None:
            intDataNeeded = True
            if not timeAdded and not speedAdded:
                q = q.filter("intData >=", elemDict['minIntData']).filter("intData <=", elemDict['maxIntData'])
                intDataAdded = True
            
        # add float data range if time and speed range not specified
        floatDataNeeded = False
        floatDataAdded = False
        if 'minFloatData' in elemDict and elemDict['minFloatData'] != None:
            floatDataNeeded = True
            if not timeAdded and not speedAdded:
                q = q.filter("floatData >=", elemDict['minFloatData']).filter("floatData <=", elemDict['maxFloatData'])
                floatDataAdded = True
            
        # execute the query (with the location data if it is present)
        results = None
        if 'minLatitude' in elemDict:
            # note: latitude in [-90, 90], longitude in [-180, 180]
            results = Measurement.bounding_box_fetch(
                        q,
                        geotypes.Box(elemDict['maxLatitude'], elemDict['maxLongitude'], elemDict['minLatitude'], elemDict['minLongitude']),
                        max_results=500) # 500 is arbitrary; we can play with this number
        else:
            results = q.fetch(500)
            
        # iterate through results if they exist and get their XML representation
        allResults = ''
        if results != None and results:
            for result in results:
                # filter on quantities that GAE couldn't get to
                toAdd = True
                if speedNeeded and not speedAdded:
                    if not ('minSpeed' in elemDict and result.speed >= elemDict['minSpeed'] and result.speed <= elemDict['maxSpeed']):
                        toAdd = False
                if intDataNeeded and not intDataAdded:
                    if not ('minIntData' in elemDict and result.intData >= elemDict['minIntData'] and result.intData <= elemDict['maxIntData']):
                        toAdd = False
                if floatDataNeeded and not floatDataAdded:
                    if not ('minFloatData' in elemDict and result.floatData >= elemDict['minFloatData'] and result.floatData <= elemDict['maxFloatData']):
                        toAdd = False
                if toAdd:
                    allResults += self.measurementToXML(result) + '\n'
        return allResults
                     
    # given a measurement, return an XML representation
    def measurementToXML(self, meas):
        e = Element('measurement')
        
        # add device id
        deviceId = Element('deviceId')
        deviceId.text = meas.deviceId
        e.append(deviceId)
        
        # add device kind
        deviceKind = Element('deviceKind')
        deviceKind.text = meas.deviceKind
        e.append(deviceKind)
        
        # add time
        myTime = Element('measurementTime')
        myTime.text = str(senselib.toUnixTime(meas.time))
        e.append(myTime)
        
        # add speed
        speed = Element('speed')
        speed.text = str(meas.speed)
        e.append(speed)
        
        # add sensor kind
        sensorKind = Element('sensorKind')
        sensorKind.text = meas.sensorKind
        e.append(sensorKind)
        
        # add sensor data
        data = Element('data')
        data.text = str(meas.data)
        e.append(data)
        
        # convert to string and return
        xmlstr = tostring(e)
        if xmlstr != None:
            return xmlstr
        else:
            return ''
    
    # Given a tree, return a dict that summarizes the data
    def treeToDict(self, elemTree):
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
            sp['deviceKindList'] = []
            for deviceKind in deviceKindList:
                if self.validateElement(deviceKind):
                    sp['deviceKindList'].append(deviceKind.text)
        
        # get all of the deviceIds to lookup
        deviceIds = elemTree.find('deviceIds')
        if deviceIds:
            deviceIdList = deviceIds.findall('deviceId')
            sp['deviceIdList'] = []
            for deviceId in deviceIdList:
                if self.validateElement(deviceId):
                    sp['deviceIdList'].append(deviceId.text)
        
        # get all of the sensorKinds to lookup
        sensorKinds = elemTree.find('sensorKinds')
        numSensors = 0
        sensorText = ""
        if sensorKinds:
            sensorKindList = sensorKinds.findall('sensorKind')
            sp['sensorKindList'] = []
            for sensorKind in sensorKindList:
                if self.validateElement(sensorKind):
                    sp['sensorKindList'].append(sensorKind.text)
                    numSensors += 1
                    sensorText = sensorKind.text
        
        # check if we can support data (only support for single sensor queries)
        sensorType = None
        if numSensors == 1:
            sensorType = sensorChooser.getSensorObject(sensorText).getDataType()
            
        # get data as a list
        if sensorType == DataType.List:
            dataValues = elemTree.find('dataValues')
            if dataValues:
                dataList = dataValues.findall('data')
                sp['dataList'] = []
                for data in dataList:
                    if self.validateElement(data):
                        sp['dataList'].append(data.text)
                        
        # get data as an int range
        elif sensorType == DataType.IntRange:
            minData = elemTree.find('minData')
            maxData = elemTree.find('maxData')
            # all must exist
            if self.validateElement(minData) and self.validateElement(maxData):
                sp['minIntData'] = int(minData.text)
                sp['maxIntData'] = int(maxData.text)
                        
        # get data as a float range
        elif sensorType == DataType.FloatRange:
            minData = elemTree.find('minData')
            maxData = elemTree.find('maxData')
            # all must exist
            if self.validateElement(minData) and self.validateElement(maxData):
                sp['minFloatData'] = float(minData.text)
                sp['maxFloatData'] = float(maxData.text)
        
        # the dictionary is set up, so return it
        return sp
        
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