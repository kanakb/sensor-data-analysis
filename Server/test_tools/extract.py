# extract.py
# an easy way to pass sensor data without a client

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

class DataExtracter(webapp.RequestHandler):
    def get(self):
        self.response.out.write("""
            <html>
                <body>
                    <form action="/getRawData" method="post">
                        Measurement Data: <br />
                        <textarea rows="40" cols="100" name="xml"></textarea>
                        <input type="submit" value="Submit" />
                    </form>
                </body>
            </html>""")

application = webapp.WSGIApplication(
                                     [('/extract', DataExtracter)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()