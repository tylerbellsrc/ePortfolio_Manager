Portfolio Manager V3.o
Author: Tyler Belluomini 1058903
Since: 2021-11-02

Program allows user to manage a portfolio of stocks and mutualFunds.
Investments are saved to file given in command line.
Investments will be attempted to read from file in command line.

Compliation
-----------
javac ePortfolio/EPortfolio.java
java ePortfolio.EPortfolio <fileName>.txt

Features
-----------
Access existing list of investments saved on disk
Buy/Sell additional stocks/mutualFunds
Search for investments in portfolio
Update prices of investments in portfolio
Compute net gain of portfolio

Limitations
-----------
All inputs have exception handling. Will prevent false inputs.

Tests
-------------
Tests were done using following stocks/mutualfunds
Stocks
Symbol: AAPL
Name: Apple Inc
Price: 150
Quantity: 100
-------
Symbol: MSFT
Name: Microsoft Inc
Price: 100
Quantity: 50

Mutual Funds
Symbol: SSETX
Name: BNY Mellon Growth Fund
Price: 50
Quantity: 100
--------
Symbol: VFV
Name: S&P/TSX Combined Index
Price: 50
Quantity: 400

These investments were also loaded onto seperate file to test program read/write
Investment file takes form:
<Investment type>
<Symbol>
<Name>
<Quantity>
<Price>
<BookValue>
Note: Initial bookvalues of stocks were set to -9.99 and mutualFunds 0.00.