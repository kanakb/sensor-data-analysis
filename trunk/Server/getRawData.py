# getRawData.py
# Given some constraints on data via XML, return raw sensor data fitting those constraints

# Python library imports
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
from importer import Measurement

# Base functionality for getting data
class GenericDataRetriever(webapp.RequestHandler):
    def post(self):
        # TODO: actually implement this

application = webapp.WSGIApplication(
                                     [('/getRawData', GenericDataRetriever)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()