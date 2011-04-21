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
    
# Manage incoming sensor data
class Importer(webapp.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        try:
            # Intialize a tree with the input XML
            measXML = self.request.get('xml')
            measurements = fromstring(measXML).getchildren()
            
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
                mEntry.speed = float(elementTree.findtext('speed'))
                mEntry.sensorKind = elementTree.findtext('sensorKind')
                mEntry.data = elementTree.findtext('data')
                # convert unix time to datetime
                utime = elementTree.findtext('measurementTime')
                mEntry.time = senselib.toDateTime(utime)
                # save to datastore if fields all valid
                if mEntry.deviceId != None and mEntry.deviceKind != None and \
                   latitude != None and longitude != None and mEntry.speed != None and \
                   mEntry.sensorKind != None and mEntry.data != None and \
                   mEntry.time != None:
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