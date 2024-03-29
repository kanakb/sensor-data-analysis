# schemas.py
# define datastore schemas common to many scripts

# GAE imports
from google.appengine.ext import db

# Third party imports
from third_party.geo import geomodel

# Schema for individual measurement data
class Measurement(geomodel.GeoModel):
    deviceId = db.StringProperty()
    deviceKind = db.StringProperty()
    time = db.DateTimeProperty()
    sensorKind = db.StringProperty()
    data = db.BlobProperty()
    floatData = db.FloatProperty()
    intData = db.IntegerProperty()
    stringData = db.StringProperty()
    
    