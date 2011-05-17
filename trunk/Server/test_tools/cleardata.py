from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db
from google.appengine.runtime.apiproxy_errors import CapabilityDisabledError

class DBCleaner(webapp.RequestHandler):
    def get(self):
        try:
            oldData = db.GqlQuery("SELECT * FROM Measurement")
            db.delete(oldData)
        except (CapabilityDisabledError, db.Error):
            pass

application = webapp.WSGIApplication(
                                     [('/cleardata', DBCleaner)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()