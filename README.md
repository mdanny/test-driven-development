The purpose of this repository is to record my experience while following Steve Freeman's and Nat Pryce's book "Growing Object-Oriented Software Guided by Tests". You can find more information about the book [here](http://www.growing-object-oriented-software.com).


## Setup of the test infrastructure
___

In order to make the unit tests pass, we first need to create an environment for them to be able to run. And, in order to set the environment for the unit tests to execute, we need to first devise the end-to-end test infrastructure, which, although requires a significant effort, will benefit the project's development in a incremental and iterative way.

In order to provide this end-to-end infrastructure, according to the authors we need to ensure the following:

* Define four components ![components](assets/diagrams/test-infrastructure.png): an XMPP message broker, a stub auction that can communicate over XMPP, a GUI testing framework (for testing the UI of our Sniper Application), and a test harness that can cope with our multithreaded, asynchronous architecture.
* Get the project under version control with an automated build/deploy/test process

Working through the first end-to-end test will force some of the structural decisions we need to make, such as packaging and deployment.

### Package selection

We need an XMPP message broker to let the application talk to the stub auction house (Fake Auction Server). The authors have chosen an open-source implementation called [Openfire](https://www.igniterealtime.org/projects/openfire/) for the server part, and [Smack](https://www.igniterealtime.org/projects/smack/) for its client counterpart (the client library).

The authors have also chosen to use [WindowLicker](https://code.google.com/archive/p/windowlicker/). It supports the requirement of having a high-level test framework that can work with Swing and Smack, both of which are multithreaded and event-driven. However due to its discontinued support, WindowLicker shouldn't be used in a production environment - rather, it should be used as a tool that facilitates support in understanding the testing using the TDD approach.

> **_NOTE:_**
End-to-end testing for event-based systems, such as our Sniper, has to cope with asynchrony. The tests run in parallel with the application and do not know precisely when the application is or isn’t ready. This is unlike unit testing, where a test drives an object directly in the same thread and so can make direct assertions about its state and behavior.
An end-to-end test can’t peek inside the target application, so it must wait to detect some visible effect, such as a user interface change or an entry in a log. The usual technique is to poll for the effect and fail if it doesn’t happen within a given time limit.

In our case, both Swing (UI) and the messaging infrastructure (XMPP client-server architecture) are asynchronous, so using WindowLicker (which polls for values) to drive the Sniper covers the natural asynchrony of our end-to-end testing.

### Ready to start

First, we are interested to test a full slice of the application we want to develop - that is, the Sniper service, which will check the auction house (real environment) for items, place a(multiple) bid(s) under the desired item(s), and while wait until the auction is stopped, increment the bidding price until a stopping point or until the auction is closed. 

The first "end-to-end" slice will be just the use case when the Sniper will connect to the auction server and wait for some time until it closes.
![first-test](assets/diagrams/joining-lost.png)

The test we have added `sniperJoinsAuctionUntilAuctionCloses()` isn't really and end-to-end, because it doesn't use the real auction server. Our strategy is to first use the Stub Auction server, make the tests pass, and afterwards use the real environment. 


### Building the test rig
___
The authors' intent is to setup an infrastructure, that will start up the OpenFire server and create accounts for the Sniper and the auction at the start of each test run. Each test will start instances of the application and the fake auction, and then test their communication through the server. At first, the server will be run on the same host as the application and client. Later on, as the infrastructure stabilizes, we can consider running different components on different machines, which will be a better match to the real deployment.

### Server Setup

Setting Up the Openfire Server

Note that the server doesn't come bundled with JRE/JDK and it requires it to run. In my case, I experienced issues on my macbook Air M2 running on macOS Ventura 13.2.1 right after the installation. The system preference OpenFire pane produces an error after being bootstrapped. In the end I had to do the following steps, so that I could start the OpenFire server locally from the terminal:

1. Locate the openfire installation on my system - in my case it is: `/usr/local/openfire`
2. Set write and execute permissions on the /bin and /conf directories by running:
`sudo chmod -R 755 /usr/local/openfire/*`
3. Overwrite the **JAVA_HOME** variable in the `/bin/openfire.sh` file (on my system I'm using sdkman to manage multiple java versions, so for running the server, I overwrote the script with the following value):
`JAVA_HOME=/path/to/user/root/.sdkman/candidates/java/8.0.362-zulu/zulu-8.jdk/Contents/Home`
4. Execute (with admin priviledge) the `openfire.sh` script from the bin directory:
`sudo ./openfire.sh`. In my case, running without sudo, doesn't set the home directory and the server cannot start as a consequence.
5. The terminal logs should show something similar to below:
`Openfire 4.7.4 [Mar 19, 2023 11:36:05 PM]
Admin console listening at http://daniels-air:9090
Successfully loaded plugin 'admin'.
` 

At the time of writing, I am using version 4.7.4 of Openfire. For these end-to-end tests, we set up our local server with three user accounts and passwords (provided you login to the admin console on the host:port combination listed in the logs):
sniper
sniper
auction-item-54321
auction
auction-item-65432
auction

In addition to the users setup, we set the openfire server to not store offline messages, because we don't have any persistent state. To do that, navigate to Server Settings > Offline Message Policy > Drop. Also, we set the resource policy to "Never kick", which will not allow a new resource to log in if there's a conflict. For that, navigate to Server Settings > Resource Policy > Never Kick.

Finally, we set up an instance of Openfire on our local host that hosts the XMPP broker. The Sniper and fake auction in our end-to-end tests, even though they’re running in the same process, will communicate through this server. We also set up logins to match the small number of item identifiers that we’ll be using in our tests. 

Now we're ready to run our first failing tests.
