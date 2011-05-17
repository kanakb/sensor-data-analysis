from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.api import urlfetch

import urllib

class HeatgridRefresher(webapp.RequestHandler):
    # Generate a new heatgrid (intended to be a cron job)
    def get(self):
        try:
            form_fields = {
                "xml": '''
                <measurements>
                    <measurement>
                        <minLatitude>34.0389</minLatitude>
                        <maxLatitude>34.0761</maxLatitude>
                        <minLongitude>-118.4838</minLongitude>
                        <maxLongitude>-118.3901</maxLongitude>
                        <sensorKinds>
                            <sensorKind>volume</sensorKind>
                        </sensorKinds>
                    </measurement>
                </measurements>
                ''',
                "xDim": "20",
                "yDim": "20"
            }
            form_data = urllib.urlencode(form_fields)
            result = urlfetch.fetch(url='http://sensor-analysis.appspot.com/heatgridGen',
                        payload=form_data,
                        method=urlfetch.POST,
                        headers={'Content-Type': 'application/x-www-form-urlencoded'})
        except:
            pass

application = webapp.WSGIApplication(
                                     [('/refreshHeatgrid', HeatgridRefresher)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
    