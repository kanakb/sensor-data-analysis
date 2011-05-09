# heatgridGen.py
# generate a file for an arbitrary heatgrid

# Python library imports
from xml.etree.ElementTree import ElementTree
from xml.etree.ElementTree import Element
from xml.etree.ElementTree import fromstring
from xml.etree.ElementTree import tostring

# GAE imports
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app