# createPoints.py
# Quickly generate some random test data for heatmap purposes

# Python imports
import random
import datetime

# GAE imports
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db
from google.appengine.runtime.apiproxy_errors import CapabilityDisabledError

# Application imports
from lib.schemas import Measurement
from lib import senselib

class PointCreator(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        try:
            minLat = float(self.request.get('minLat'))
            maxLat = float(self.request.get('maxLat'))
            deltaLat = maxLat - minLat
            minLon = float(self.request.get('minLon'))
            maxLon = float(self.request.get('maxLon'))
            deltaLon = maxLon - minLon
            
            # Generate 500 data points
            for i in range(500):
                # get an arbitrary location in the box
                latitude = random.random() * deltaLat + minLat
                longitude = random.random() * deltaLon + minLon
                m = Measurement(location=db.GeoPt(latitude, longitude))
                if self.request.get('id'):
                    m.deviceId = self.request.get('id')
                else:
                    m.deviceId = senselib.toHash('test')
                m.deviceKind = 'Test Device'
                m.time = datetime.datetime.now()
                m.sensorKind = 'volume'
                # random reading from 1 to 10
                m.floatData = random.random() * 10. + 1
                m.data = str(m.floatData)
                m.update_location()
                m.put()
            
            self.response.out.write('SUCCESS')
        except:
            pass
            
application = webapp.WSGIApplication(
                                     [('/createPoints', PointCreator)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
    