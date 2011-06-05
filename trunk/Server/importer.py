# importer.py
# Receive and store phone sensor data

# Python library imports
import datetime
from xml.etree.ElementTree import fromstring
from xml.etree.ElementTree import ElementTree

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
from sensors import sensorChooser
from sensors.genericSensor import DataType
    
# Manage incoming sensor data
class Importer(webapp.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        try:
            # Intialize a tree with the input XML
            measXML = self.request.get('xml')
            m1 = fromstring(measXML)
            
            # support taking in single or multiple measurements
            measurements = []
            if m1.tag == 'measurement':
                measurements.append(m1)
            else:
                measurements = m1.getchildren()
            
            # Insert each measurement into the datastore
            for m in measurements:
                # Making sure input is a set of measurement tags
                if m.tag != 'measurement':
                    raise ValueError
                    
                elementTree = ElementTree(m)
                # store latitude/longitude as a single geo point
                latitude = float(elementTree.findtext('latitude'))
                longitude = float(elementTree.findtext('longitude'))
                mEntry = Measurement(location=db.GeoPt(latitude, longitude))
                # some fields can be saved as-is
                mEntry.deviceId = elementTree.findtext('deviceId')
                mEntry.deviceKind = elementTree.findtext('deviceKind')
                mEntry.sensorKind = elementTree.findtext('sensorKind')
                mEntry.data = elementTree.findtext('data')
                # convert unix time to datetime
                utime = elementTree.findtext('time')
                mEntry.time = senselib.toDateTime(utime)
                # save to datastore if fields all valid
                if mEntry.deviceId != None and mEntry.deviceKind != None and \
                   latitude != None and longitude != None and \
                   mEntry.time != None:
                    # add searchable data if possible
                    if mEntry.sensorKind != None and mEntry.data != None:
                        dataType = sensorChooser.getSensorObject(mEntry.sensorKind).getDataType()
                        if dataType == DataType.IntRange:
                            mEntry.intData = int(mEntry.data)
                        elif dataType == DataType.FloatRange:
                            mEntry.floatData = float(mEntry.data)
                        elif dataType == DataType.List:
                            mEntry.stringData = str(mEntry.data)
                    # update location and save everything
                    mEntry.update_location()
                    mEntry.put()
                
            self.response.out.write("SUCCESS")
            
        except (TypeError, KeyError, ValueError, CapabilityDisabledError):
            self.response.out.write("FAILURE")

application = webapp.WSGIApplication(
                                     [('/import', Importer)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()