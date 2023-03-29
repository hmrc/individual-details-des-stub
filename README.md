# individual-details-des-stub

[![Build Status](https://travis-ci.org/hmrc/individual-details-des-stub.svg?branch=master)](https://travis-ci.org/hmrc/individual-details-des-stub) [ ![Download](https://api.bintray.com/packages/hmrc/releases/individual-details-des-stub/images/download.svg) ](https://bintray.com/hmrc/releases/individual-details-des-stub/_latestVersion)

This is the stateful Stub of the Data Exchange Service (DES) for retrieving/storing test individual details.
It allows third party developers to create their own test individuals on the External Test Environment.

It is a shared Stub for both openid-connect-userinfo and citizen-details.

### Running tests

Unit, integration and component tests can be run with the following:

    sbt test it:test component:test

### License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
