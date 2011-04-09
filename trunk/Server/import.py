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

# Application imports
from lib import senselib

# Schema for individual measurement data
class Measurement(db.Model):
    deviceId = db.StringProperty()
    deviceKind = db.StringProperty()
    latitude = db.FloatProperty()
    longitude = db.FloatProperty()
    time = db.DateTimeProperty()
    sensorKind = db.StringProperty()
    sensorData = db.BlobProperty()
    
# Manage incoming sensor data
class Importer(webapp.RequestHandler):
    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        try:
            # Intialize a tree with the input XML
            measXML = '<measurementData>\n\n'
            measXML += self.request.get('measurementData')
            measXML += '\n\n</measurementData>'
            measurements = fromstring(measXML).getchildren()
            
            # Insert each measurement into the datastore
            for m in measurements:
                # Making sure input is a set of measurement tags
                if m.tag != 'measurement':
                    raise ValueError
                    
                elementTree = ElementTree(m)
                mEntry = Measurement()
                mEntry.deviceId = senselib.toHash(elementTree.findtext('deviceId'))
                mEntry.deviceKind = elementTree.findtext('deviceKind')
                mEntry.latitude = float(elementTree.findtext('latitude'))
                mEntry.longitude = float(elementTree.findtext('longitude'))
                mEntry.sensorKind = elementTree.findtext('sensorKind')
                mEntry.sensorData = elementTree.findtext('sensorData')
                # convert unix time to datetime
                utime = elementTree.findtext('measurementTime')
                mEntry.time = senselib.toDateTime(utime)
                # save to datastore if fields all valid
                if mEntry.deviceId != None and mEntry.deviceKind != None and \
                   mEntry.latitude != None and mEntry.longitude != None and \
                   mEntry.sensorKind != None and mEntry.sensorData != None and \
                   mEntry.time != None:
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