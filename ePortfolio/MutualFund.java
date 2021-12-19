package ePortfolio;

/**
 * Object for MutualFund. Contains all info needed.
 * @author Tyler Belluomini
 * @since 2021-09-28
 */
public class MutualFund extends Investment {

    final double FEE = 45;

    public MutualFund(String symbol, String name, int quantity, double price) {
        super(symbol, name, quantity, price);
        this.bookValue = 0;
    }

    public MutualFund(String symbol, String name, int quantity, double price, double bookValue) {
        super(symbol, name, quantity, price);
        this.bookValue = bookValue;
    }

    /**
     * Completes the sale of mutualFund
     * @param price Price to sell mutualFund at
     * @param quantity Quantity of shares to sell
     */
    public void sell(double price, int quantity) {
        this.bookValue -= FEE;
        updatePrice(price);
        this.quantity -= quantity;

        return;
    }

}
