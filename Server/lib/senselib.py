# Helper functions for the sensor analysis server
import hashlib
import datetime
import time

# Obtain a salted hash of some data
def toHash(s):
    # todo: throw the salt into the datastore
    salt = 'xmGskdd2gv3UZHpxOzq17FvndKPlr'
    hash = hashlib.md5( salt + str(s) ).hexdigest()
    return str(hash)
    
# Convert unix time to datetime
def toDateTime(t):
    return datetime.datetime.fromtimestamp(float(t))

# Convert datetime to unix time
def toUnixTime(t):
    return time.mktime(t.timetuple()) + (1e-6) * t.microsecond
