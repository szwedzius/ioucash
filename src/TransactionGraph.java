import java.time.LocalDate;
import java.util.*;
public class TransactionGraph {
    ArrayList<Transaction> listOfTransactions;
    ArrayList<User> users;
    public TransactionGraph(ArrayList<Transaction> listOfTransactions, ArrayList<User> users) {
        this.listOfTransactions = listOfTransactions;
        this.users = new ArrayList<User>(); // inicjalizacja pola users
    }
    public void addTransaction(Transaction transaction) {
        // Sprawdź, czy użytkownicy transakcji są już na liście użytkowników
        if (!users.contains(transaction.getSender())) {
            users.add(transaction.getSender());
        }
        if (!users.contains(transaction.getReceiver())) {
            users.add(transaction.getReceiver());
        }
        // Zaktualizuj saldo użytkowników
        User sender = transaction.getSender();
        User receiver = transaction.getReceiver();
        double amount = transaction.getAmount();

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        // Dodaj transakcję do listy transakcji
        listOfTransactions.add(transaction);
    }

    public List<Transaction> simplify(ArrayList<User> users, ArrayList<Transaction> listOfTransactions) {
        List<Transaction> simplifiedList = new ArrayList<>();

        // Znajdź niepołączone podgrafy
        List<Set<User>> subGraphs = findSubGraphs(users, listOfTransactions);

        // Dla każdego niepołączonego podgrafu znajdź najkrótszą ścieżkę i dokonaj uproszczenia
        for (Set<User> subGraph : subGraphs) {
            ArrayList<User> subGraphUsers = new ArrayList<>(subGraph);

            while (findUserWithMaxBalance(subGraphUsers) != null || findUserWithMinBalance(subGraphUsers) != null) {

                User debitor = findUserWithMaxBalance(subGraphUsers);
                User borrower = findUserWithMinBalance(subGraphUsers);
                double debitorbalance = debitor.getBalance();
                double borrowerbalance = borrower.getBalance();
                double transactionsize = 0;
                if (Math.abs(debitorbalance) > Math.abs(borrowerbalance)) {
                    transactionsize = Math.abs(borrower.getBalance());
                } else {
                    transactionsize = Math.abs(debitor.getBalance());
                }
                Transaction Simplifiedtransaction = new Transaction(0, debitor, borrower, transactionsize, LocalDate.now());
                simplifiedList.add(Simplifiedtransaction);

                debitor.setBalance(debitorbalance - transactionsize);
                borrower.setBalance(borrowerbalance + transactionsize);
            }
        }
        /*for (Transaction transaction : simplifiedList) {
            System.out.println(transaction.toString());
        }

         */
        return simplifiedList;
    }
    public List<Set<User>> findSubGraphs(ArrayList<User> users, ArrayList<Transaction> listOfTransactions) {
        List<Set<User>> subGraphs = new ArrayList<>();

        // Create a map that associates each user with the set of users they have transacted with
        Map<User, Set<User>> userToFriends = new HashMap<>();
        for (User user : users) {
            userToFriends.put(user, new HashSet<>());
        }
        for (Transaction transaction : listOfTransactions) {
            User sender = transaction.getSender();
            User receiver = transaction.getReceiver();
            userToFriends.get(sender).add(receiver);
            userToFriends.get(receiver).add(sender);
        }

        // Traverse the graph to find all disconnected subgraphs
        Set<User> visitedUsers = new HashSet<>();
        for (User user : users) {
            if (!visitedUsers.contains(user)) {
                Set<User> subGraph = new HashSet<>();
                traverseGraph(user, userToFriends, visitedUsers, subGraph);
                subGraphs.add(subGraph);
            }
        }
        return subGraphs;
    }

    private void traverseGraph(User user, Map<User, Set<User>> userToFriends, Set<User> visitedUsers, Set<User> subGraph) {
        visitedUsers.add(user);
        subGraph.add(user);
        for (User friend : userToFriends.get(user)) {
            if (!visitedUsers.contains(friend)) {
                traverseGraph(friend, userToFriends, visitedUsers, subGraph);
            }
        }
    }

    public User findUserWithMaxBalance(ArrayList<User> users) {
            User maxUser = null;
            double maxBalance = 0.0;
            for (User user : users) {
                if (user.getBalance() > maxBalance) {
                    maxBalance = user.getBalance();
                    maxUser = user;
                }
            }
            return maxUser;
        }
        public User findUserWithMinBalance(ArrayList<User> users) {
            User minUser = null;
            double minBalance = 0.0;
            for (User user : users) {
                if (user.getBalance() < minBalance) {
                    minBalance = user.getBalance();
                    minUser = user;
                }
            }
            return minUser;
        }
        public ArrayList<Transaction> giveTransactions() {
            return listOfTransactions;
        }

    public List<Transaction> getTransactionsForUser(User user) {
        List<Transaction> transactionsForUser = new ArrayList<>();
        for (Transaction transaction : listOfTransactions) {
            if (transaction.getSender().equals(user) || transaction.getReceiver().equals(user)) {
                transactionsForUser.add(transaction);
            }
        }
        return transactionsForUser;
    }

}



