# Helper functions for the sensor analysis server
import hashlib
import datetime
import time

# Obtain a salted hash of some data
def toHash(s):
    salt = 'xmGskdd2gv3UZHpxOzq17FvndKPlr'
    hash = hashlib.md5( salt + str(s) ).hexdigest()
    return str(hash)
    
# Convert unix time to datetime
def toDateTime(unixTime):
    #todo: implement this
    return ""

# Convert datetime to unix time
def toUnixTime(dateTime):
    #todo: implement this
    return ""