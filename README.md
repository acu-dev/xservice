xService
========

xService is a java web-app that provides RESTful services to access Xythos WFS 7.x data. It provides services for the
iOS app xDrive to connect to Xythos WFS.

This project is still actively being developed so it may change at any time.

Todo:

* Add file manipulation (upload, move, copy, delete)
* Add comments and other metadata access

Installation
------------

1. Unpack the `xservice-x.x.war` into your `{xythos-base}/wfs-7.x.x/webapps` folder
1. Add the context to `{xythos-base}/server-7.x.x/conf/server.xml`:
	<!-- xService - RESTful services for xDrive -->
	<Context docBase="xservice" path="/xservice"/>
1. Edit the server details in `{xythos-base}/wfs-7.x.x/webapps/xservice/info.json` to match your installation
1. Restart Xythos and visit http(s)://your-xythos-url.tld/xservice to verify

License
-------

Copyright (c) 2011, Abilene Christian University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Abilene Christian University nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.