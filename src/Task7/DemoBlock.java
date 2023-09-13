package Task7;
// Работает хуево, перепишу на go
import java.util.ArrayList;
import java.util.Date;
import java.security.MessageDigest;

class Block {
    public String hash;
    public String previousHash;
    protected String data;
    private long timeStamp;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        data
        );
        return calculatedHash;
    }
}

class Blockchain {
    protected ArrayList<Block> blockchain = new ArrayList<>();

    public void addBlock(Block newBlock) {
        blockchain.add(newBlock);
    }

    public Block getLatestBlock() {
        return blockchain.get(blockchain.size() - 1);
    }

    public boolean isChainValid() {
        for (int i = 1; i < blockchain.size(); i++) {
            Block currentBlock = blockchain.get(i);
            Block previousBlock = blockchain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                return false;
            }

            if (!currentBlock.previousHash.equals(previousBlock.hash)) {
                return false;
            }
        }
        return true;
    }
}



class StringUtil {
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

public class DemoBlock {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();

        blockchain.addBlock(new Block("Первый блок", "0"));
        blockchain.addBlock(new Block("Второй блок", blockchain.getLatestBlock().hash));
        blockchain.addBlock(new Block("Третий блок", blockchain.getLatestBlock().hash));

        System.out.println("Блокчейн валиден: " + blockchain.isChainValid());

        // Изменение данных в блоке для проверки целостности
        blockchain.blockchain.get(1).data = "Измененные данные";
        System.out.println("Блокчейн валиден: " + blockchain.isChainValid());
    }
}
