package ePortfolio;

/**
 * Object for all investments. Superclass of stock and mutualFund
 * @author Tyler Belluomini
 * @since 2021-10-25
 */
public abstract class Investment {
    
    protected int quantity;
    protected double price, bookValue;
    protected String name, symbol;

    public Investment(String symbol, String name, int quantity, double price) {
        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
        this.price = price;

    }

    /**
     * Returns the value of the stock symbol.
     * @return  the value of the symbol for stock
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Returns the name of the stock
     * @return  the name of the stock
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the quantity of stock owned
     * @return  quantity of stock owned
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * Retrieves the latest price of the stock
     * @return  current price of stock
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * Retrieves the book value of stock owned
     * @return  value of stock held
     */
    public double getBookValue() {
        return this.bookValue;
    }

    /**
     * Allows user to buy additional investment. Updates relavent data.
     * @param price New price of Investment (Updates bookvalue of owned investment)
     * @param quantity Quantity of NEW shares to be purchased
     */
    public void updateQuantity(double price, int quantity) {
        this.bookValue += (this.quantity * price) - (this.quantity * this.price);
        this.price = price;
        this.quantity += quantity;
        return;
    }

    /**
     * Updates the price of Investment, along with Book Value
     * @param newPrice New price of Investment
     */
    public void updatePrice(double newPrice) {
        this.bookValue += (this.quantity * newPrice) - (this.quantity * this.price);
        this.price = newPrice;

        return;
    }

    /**
     * Completes the sale of investment
     * @param price Price to sell Investment at
     * @param quantity Quantity of shares to sell
     */
    public void sell(double price, int quantity) {
        updatePrice(price);
        this.quantity -= quantity;
        return;
    }

    /**
     * Prints stock info
     */
    public void printInvestment() {
        System.out.println("------------------------------");
        System.out.println(this.symbol);
        System.out.println(this.name);
        System.out.println("Price: " + this.price);
        System.out.println("Quantity Owned: " + this.quantity);

        return;
    }

    /**
     * Returns stock as multi-line string
     * @return stock as string
     */
    public String toString() {
        return ("Symbol: " + this.symbol + "\nName: " + this.name + "\nPrice: $" + String.format("%.2f", this.price) + 
        "\nQuantity: " + this.quantity + "\nGains: $" + String.format("%.2f", this.bookValue) + "\n");
    }
}
