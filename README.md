# GUIzilla
GUI browser built on client-server architecture

INSTRUCTIONS FOR USE:

There are two parts of our GUIzilla project that the user could interact with: the client (Guizilla.scala) and the server (GuiServer.scala). Technically, the user could interact with either separately, or both together.

In order to interact with (i.e. run) our GuiServer, a user would do so by navigating to the bin folder in the scalaproject directory and running the following command: "scala guizilla.sol.server.GuiServer" with no other arguments. This will start the server and it will run continuously until you exit the terminal. From here the user can access from our GUIzilla client, or from other browsers. In order to access our GuiServer specifically, the user simply has to denote "localhost" as the hostname in the url path if they are using a browser on the same machine (or the name of the machine if they are on another computer in the department).

From any browser, the user can access these pages in GuiServer:
- Index (a page with links to all the other pages we implement in our server in it)
- FirstLast (a page that takes you full name and asks you stuff)
- Calendar (a page for organizing a very rudimentary calendar for today and tomorrow’s activities)
- Search (a page for Search integration: enter a query; see search results; follow links to those pages)
- AddTwo (a page that adds two numbers, taken from the GUIzilla pdf)

In order to access FirstLast, for example, the user would enter into the URL field: "http://localhost/FirstLast"

In order to also interact with our Guizilla client, a user would do so by navigating to the bin folder in the scalaproject directory and running the following command: "scala guizilla.sol.client.Guizilla" with no other arguments. (Importantly, if GuiServer.scala is already running, the user will have to open up a new terminal to run Guizilla.scala.) This will bring up our hardcoded Guizilla homepage. From here, the user can perform one of many actions on the GUI interface, doing so by clicking buttons and typing text into text fields as needed. Here are the actions:

(1) Click the "Back" button to go to the previous page visited (does not do anything if there are no pages to go back to)

(2) Enter a url for a new page in the top text field bar (and clicking the "==>" button)

(3) Click the "Quit" button to exit the program

(4) (Optional) If there is a link the user can click it and be taken to the page associated with the link (via a GET Request)

(5) (Optional) If there is a input field for a form (an empty text field), the user can click into it and enter text. The user can then click the "Submit" button, and a form will be submitted (via a POST Request) to the associated url address with any inputted form data.

Incorrect url input will be handled and an informative message will be sent to the user, after which the user can enter a new URL. For example, a bad url will return the “Invalid URL” page. Or, a bad host name, for example, will similarly give an "Unknown host" page. The user can then enter a new URL.

The user can navigate around the chosen server using the browser to visit links, fill out input, submit forms, and visiting new (valid) URLs as long as desired until clicking the "Quit" button.

NOTE: The user can use the Guizilla client independent from our GuiServer and access other host servers on the network. They just cannot go to pages on our GuiServer or visit localhost if GuiServer is not running in the background.
_________________________________________________________________________________________

DESIGN OVERVIEW:

General Notes:

Our design has two main parts, a client and a server. The client is the GUI interface that the user interacts with. It handles the actions and the connections with the server. The server it takes the client’s request, runs the program named in the request, and then sends the program’s output back to the client. The design of these two parts are explained in more detail:

CLIENT: Guizilla.scala

Our guizilla.sol.client folder has five things in it.

First, there is Client.scala. This is essentially just the exact same code from our Sparkzilla client. The only two differences are that it does not have the REPL loop anymore, and we also cache the URL in our PrevPages list (along with the page representation and the host name as before) because we need it later. This makes sense, as the Guizilla browser has all the same base-level networking functionality as before, just represented differently.

Second, there is Controller.scala. This class extends Client, and is used in order to interact with our GUI browser window. It has access to every method in Client, but also implements specific methods that are necessary in order to render our GUI javafx interface. For example, pressing the "Back" button, the "==>" button, or the "Quit" button calls the @FML goBack(event: ActionEvent), @FML goToURL(event: ActionEvent), and @FML quit(event: ActionEvent), respectively. These methods which preform the necessary functionalities for the USER interface, essentially re-using code that would have been in our REPL loop in Sparkzilla. The renderPage() method is also new, but is fairly self explanatory. We need to call this at the end of each action method because we cannot take just call it at the top of a while loop (since there isn’t one). Rendering a page utilizes the getVBox() and setVBox(vbx: VBox) methods as this is a private field our Controller.  Finally, submitForm() and followLink() methods again re-use code from Sparkzilla that would have been in our REPL loop for when a user clicks a submit button or a link in the GUI.

Third, there is the Guizilla.scala program. This class loads our Gui window up, instantiates a controller, sets the title, sets the scene, and finally shows the entire window. All of this happens in a single method, start(), which is called when the application is launched in its main method. This is pretty self-explanatory and isn’t anything new, as it is mostly re-used code from the OfficerRacket lab.

Fourth, there is the parser folder. Again, this is almost exactly the same as our Parser from Sparkzilla, as all of the HTML parsing, active element handling, etc. are the same from the previous project. The only main difference of note is the render() method in each case class in the class hierarchy. Because it cannot render to a terminal, render() does not output a string anymore. Rather, render(ctrl: Controller) takes in a Controller and adds/removes the necessary javafx elements to the Controller’s VBox (such as TextFields, Text, or HyperLinks), as these are actually what must rendered in the GUI user interface. These render methods also connect the Submit and HyperLink buttons to the Controller, and implement the necessary functionality to call submitForm() and followLink() in the Controller. Lastly, the Input field case class has a special implementation: it has a ChangeListener in the TextField in order to detect when a user types anything into it, and updates our internal storage of the input String in our client accordingly.

Finally, there is tester.fxml. This is the fxml file that represents our GUI. This is where we setup the basics of our GUI such as the back button, url bar, forward button and quit button, which are all there all of the time and are placed within an HBox. We also have a VBox in our fxml. The VBox will be reset every time we go to a new page, and as we go through rendering the page we will add the new elements of the page to the VBox so that they appear in our GUI. Before loading a new page we clear this VBox.

SERVER: GuiServer.scala

Our guizilla.sol.sevrer has two things in it.

First, there is GuiServer.scala. This is our server, and at the base level it functions just like the Servers from lab. It runs continuously via a never-ending while loop via the runServer() method, taking in input from a socket, interpreting it via the checkTypeRequest() method (which in turn calls many other methods) and finally sets a response into the OutputStream before shooting down the output and listening for more input. Also, at the top of the GuiServer.scala, we have all the hard-coded HTML files (as Strings) representing various error messages necessary to throw. GuiServer also has a sessionMap[String, Page] which is how we store all the instances of Pages that users could be interacting with in order to go back properly and support multiple users.

More specifically, here’s what happens when we receive a GET or POST request. checkTypeRequest() checks if the method is a get or post by checking the first line of the request, sending a badRequestResponse if neither, and then calls parseRequest(). parseRequest() takes in two arguments, the request, and a boolean repressing if it is a get request (true if so, and false otherwise). If it is a POST request, parseRequest() calls buildInputMap() in order to parse the form input at the bottom of the POST request, which we get by matching onto Content-Length in the form and then reading that much from the last line. buildInputMap() gives us our input Map[String,String]. parseRequest() continues by checking that the request has HTTP/1.0 or HTTP/1.1, and then parses the URL path from the request. It parses the path for the method, and if there is an id, then it gets the corresponding page form the sessionMap, clones it, makes a new id, and calls the method on the cloned page with the inputs and NewId (using reflection). This returns the response, which is sent back to the client. Also, the clonedPage and the new id are put in the Session Map. Instead, if the url path has a class name, it instantiates that class, makes a new id, puts both in the sessionMap, and then does the same action as before: calling the method on the page with the inputs and the newId  (using reflection). Importantly, if it is a GET request, inputs will just be an empty map. If it is a POST request, inputs will be the filled-out map at this point. All the while, we catch errors/exceptions wherever they might appear (which usually means some sort of bad request, page not found, or method issue), and in these cases, our Hard-Coded Error pages are sent back to the client to make sure that the program does not crash.

Second, there is a Pages folder. This contains all the pages implemented in our server, which are again:

- Index (a page with links to all the other pages we implement in our server in it)
- FirstLast (a page that takes you full name and asks you stuff)
- Calendar (a page for organizing a very rudimentary calendar for today and tomorrow’s activities)
- Search (a page for Search integration: enter a query; see search results; follow links to those pages)
- AddTwo (a page that adds two numbers, taken from the GUIzilla pdf)

Index is a static page, but all the others implement Dynamic Content, as described in their descriptions. All of them extend Page, and are modeled after the basic structure of the AddTwo from the Guizilla pdf, which have a defaultHandler() method, and all methods send back Strings representing and HTML file pointing directing the next POST request to the next method in the page. The user can visit one by typing in "http://localhost/Search" for example.

Search has a couple extra implementations, as it must integrate the Search interface, so the page itself opens sockets to access the Search Servers which we send the name of a query, or the title of a page, and which return the top titles or the text of a page, respectively. Our search page takes the User input, and performs the necessary tasks to send and parse the responses of the Search servers. The other important thing is the data storage. We store the names of all the titles in an array, and have methods which refer to sending whatever may be stored that index itself, not actually the title. This is because there is no way for the link itself in the Search page to really know what the title actually is. But, by the time someone clicks the link, the titles will be stored in the Page, so the method for that link number can refer to the data storage, and perform the necessary functions that way. That is how the Search page works.
