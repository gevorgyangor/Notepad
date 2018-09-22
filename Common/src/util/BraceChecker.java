package notepad.util;

public class BraceChecker {

    public static final int OPENED_BUT_NOT_CLOSED = 0;
    public static final int CLOSED_BUT_NOT_OPENED = 1;
    public static final int OPENED_BUT_CLOSED_ANOTHER = 3;
    private static BraceChecker instance;
    /**
     * TO store the opened brackets in text to be parsed
     */
    private StackImpl<BracketItem> stack;

    /**
     * TO store the parsing result
     */
    private String message = "No Error";

    /**
     * Error symbol index in parsing text
     */
    private int errorIndex = -1;

    /**
     * Getter method for message field
     *
     * @return the value of the message
     */
    public String getMessage() {
        return message;
    }

    public int getErrorIndex() {
        return errorIndex;
    }


    private BraceChecker() {
        stack = new StackImpl();
    }

    public boolean parse(String text) {
        boolean isPassed = true;
        int numberInLine = 0;
        int lineNumber = 0;
        BracketItem stackLastElement = null;
        char ch = 0;
        lab:
        for (int i = 0; i < text.length(); i++) {
            errorIndex++;
            ch = text.charAt(i);
            numberInLine++;
            switch (ch) {
                case '\n':
                case '\r':
                    numberInLine = 0;
                    lineNumber++;
                    break;
                case '(':
                case '{':
                case '[':
                    BracketItem item = new BracketItem(ch, numberInLine, lineNumber, i);
                    stack.push(item);
                    break;
                case ')':
                    stackLastElement = stack.pop();

                    // Must be checked whether the stackLastElement == null in order to avoid NullPointerException
                    if (stackLastElement == null || stackLastElement.getValue() != '(') {
                        isPassed = false;
                        break lab;
                    }
                    break;
                case ']':
                    stackLastElement = stack.pop();
                    if (stackLastElement == null || stackLastElement.getValue() != '[') {
                        isPassed = false;
                        break lab;
                    }
                    break;
                case '}':
                    stackLastElement = stack.pop();
                    if (stackLastElement == null || stackLastElement.getValue() != '{') {
                        isPassed = false;
                        break lab;
                    }
                    break;
            }
        }
        if (!isPassed) {
            if (stackLastElement == null) {
                message = "closed '" + ch + "' but not opened ";
            } else {
                message = "opened '" + stackLastElement.getValue() + "' but closed '" + ch + "'";
            }
        } else if ((stackLastElement = stack.pop()) != null) {
            message = "opened '" + stackLastElement.getValue() + "' but not closed";
            isPassed = false;
        }
        return isPassed;
    }

    public static BraceChecker getInstance() {
        if (instance == null) {
            synchronized (BraceChecker.class) {
                if (instance == null) instance = new BraceChecker();
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        String s = "{[()] }";
        new Thread(new Runnable() {
            @Override
            public void run() {
                BraceChecker b = BraceChecker.getInstance();
            }
        }).start();
        instance.parse(s);
        System.out.println(instance.getMessage());
    }

    public static class BracketItem {

        private char value;
        private int numberInLine;
        private int lineNumber;
        private int index;

        public BracketItem(char value, int numberInLine, int lineNumber, int index) {
            this.value = value;
            this.numberInLine = numberInLine;
            this.lineNumber = lineNumber;
            this.index = index;
        }

        public char getValue() {
            return value;
        }

        public void setValue(char value) {
            this.value = value;
        }

        public int getNumberInLine() {
            return numberInLine;
        }

        public void setNumberInLine(int numberInLine) {
            this.numberInLine = numberInLine;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
