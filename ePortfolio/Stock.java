package ePortfolio;

/**
 * Object for stock. Contains all info needed
 * @author Tyler Belluomini
 * @since 2021-09-28
 */
public class Stock extends Investment {

    final double COMMISSION = 9.99;

    public Stock(String symbol, String name, int quantity, double price) {
        super(symbol, name, quantity, price);
        this.bookValue = - COMMISSION;

    }

    public Stock(String symbol, String name, int quantity, double price, double bookValue) {
        super(symbol, name, quantity, price);
        this.bookValue = bookValue;
    }

    /**
     * Completes the sale of stock
     * @param price Price to sell stock at
     * @param quantity Quantity of stock to sell
     */
    public void sell(double price, int quantity) {
        updatePrice(price);
        this.bookValue -= COMMISSION;
        this.quantity -= quantity;
        
        return;

    }

}
