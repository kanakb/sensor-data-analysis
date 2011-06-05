from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.api import urlfetch

import urllib

class HeatgridRefresher(webapp.RequestHandler):
    # Generate a new heatgrid (intended to be a cron job)
    def get(self):
        deviceIdData = ''
        if self.request.get('id'):
            deviceIdData = '''
                    <deviceIds>
                        <deviceId>''' + self.request.get('id') + '''</deviceId>
                    </deviceIds>'''
        xmlreq = '''
            <measurements>
                <measurement>''' + deviceIdData + '''
                    <minLatitude>34.0389</minLatitude>
                    <maxLatitude>34.0761</maxLatitude>
                    <minLongitude>-118.4838</minLongitude>
                    <maxLongitude>-118.3901</maxLongitude>
                    <sensorKinds>
                        <sensorKind>volume</sensorKind>
                    </sensorKinds>
                </measurement>
            </measurements>
            '''
        form_fields = {
            "xDim": "20",
            "yDim": "20"
        }
        form_fields['xml'] = xmlreq
        form_data = urllib.urlencode(form_fields)
        result = urlfetch.fetch(url='http://[YOUR-DOMAIN-HERE].appspot.com/heatgridGen',
                    payload=form_data,
                    method=urlfetch.POST,
                    headers={'Content-Type': 'application/x-www-form-urlencoded'})
        

application = webapp.WSGIApplication(
                                     [('/refreshHeatgrid', HeatgridRefresher)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
    