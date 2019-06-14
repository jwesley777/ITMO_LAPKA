
class NoItemException extends Exception {
    public NoItemException(IHands subject, String itemName) { super(subject + " does not have " + itemName);}
}
