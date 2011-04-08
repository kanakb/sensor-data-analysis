from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

class DataAdder(webapp.RequestHandler):
    def get(self):
        self.response.out.write("""
            <html>
                <body>
                    <form action="/import" method="post">
                        Measurement Data: <br />
                        <textarea rows="20" cols="100" name="measurementData"></textarea>
                        <input type="submit" value="Submit" />
                    </form>
                </body>
            </html>""")

application = webapp.WSGIApplication(
                                     [('/insert', DataAdder)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()