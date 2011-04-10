# import.py
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

# Schema for individual measurement data
class Measurement(geomodel.GeoModel):
    id = db.StringProperty()
    deviceId = db.StringProperty()
    deviceKind = db.StringProperty()
    time = db.DateTimeProperty()
    sensorKind = db.StringProperty()
    data = db.BlobProperty()
    
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
                mEntry.id = elementTree.findtext('deviceId')
                mEntry.deviceId = senselib.toHash(elementTree.findtext('deviceId'))
                mEntry.deviceKind = elementTree.findtext('deviceKind')
                mEntry.sensorKind = elementTree.findtext('sensorKind')
                mEntry.data = elementTree.findtext('data')
                # convert unix time to datetime
                utime = elementTree.findtext('measurementTime')
                mEntry.time = senselib.toDateTime(utime)
                # save to datastore if fields all valid
                if mEntry.id != None and mEntry.deviceId != None and mEntry.deviceKind != None and \
                   latitude != None and longitude != None and \
                   mEntry.sensorKind != None and mEntry.sensorData != None and \
                   mEntry.time != None:
                    mEntry.update_location()
                    mEntry.put()
                
            self.response.out.write("SUCCESS")
            
        except (KeyError, ValueError, CapabilityDisabledError):
            self.response.out.write("FAILURE")

application = webapp.WSGIApplication(
                                     [('/import', Importer)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()