import java.util.*;

public class ClassGraph {

    private ClassNode root;

    public ClassGraph() {
        this.root = null;
    }

    public boolean hasContent() {
        return root != null;
    }

    void setRoot(ClassNode root) {
        this.root = root;
    }

    ClassNode getRoot() {
        return root;
    }

    public int getHeight() {
        return heightHelper(root, 1);
    }

    private int heightHelper(ClassNode curr, int currHeight) {
        if (curr == null || curr.getChildren() == null) {
            return 0;
        }
        if (curr.getChildren().isEmpty()) {
            return currHeight;
        } else {
            int currMax = -1;
            for (ClassNode node : curr.getChildren()) {
                currMax = Math.max(currMax, heightHelper(node, currHeight + 1));
            }
            return currMax;
        }
    }

    public void clear() {
        this.root = null;
    }

    public boolean containsVal(String code) {
        if (code.contains(" ")) {
            String[] real = code.split(" ");
            code = real[0] + real[1];
        }
        return containsValHelper(root, code);
    }

    private boolean containsValHelper(ClassNode node, String code) {
        if (!Objects.equals(node.getCode(), root.getCode())) {
            String[] real = node.getCode().split("\u00A0");
            String nCode = real[0] + real[1];
            if (nCode.equals(code)) {
                return true;
            }
        }
        for (ClassNode child : node.getChildren()) {
            if (containsValHelper(child, code)) {
                return true;
            }
        }
        return false;
    }

    public ClassNode getVal(String uuid) {
        return getValHelper(root, uuid);
    }

    private ClassNode getValHelper(ClassNode node, String uuid) {
        if (!Objects.equals(node.getCode(), root.getCode())) {
            if (node.getUUID().equals(uuid)) {
                return node;
            }
        }
        for (ClassNode child : node.getChildren()) {
            ClassNode n2 = getValHelper(child, uuid);
            if (n2 != null) {
                return n2;
            }
        }
        return null;
    }

    public int getThousandCount(int target) {
        ArrayList<String> found = new ArrayList<>();
        return thousandCountHelper(root, target, found);
    }

    private int thousandCountHelper(ClassNode node, int target, ArrayList<String> found) {
        int sum = 0;
        if (found.contains(node.getCode())) {
            return 0;
        }
        if (!Objects.equals(node.getCode(), root.getCode())) {
            sum =
                    (Integer.parseInt((node.getCode().substring(
                            node.getCode().indexOf("\u00A0") + 1
                    ))) / 1000 == target) ? 1 : 0;
        }
        for (ClassNode child : node.getChildren()) {
            sum += thousandCountHelper(child, target, found);
        }
        found.add(node.getCode());
        return sum;
    }

    public String recommendSchedule() {
        List<ClassNode> choices = new ArrayList<>();
        Queue<ClassNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            ClassNode curr = queue.poll();
            if (!curr.getCode().equals(root.getCode())) {
                if (!curr.isTaken() && curr.getInDegree() == 0) {
                    choices.add(curr);
                }
            }
            queue.addAll(curr.getChildren());
        }
        choices.sort(Comparator.comparingInt(ClassNode::getOutDegree));
        Collections.reverse(choices);
        StringBuilder ret = new StringBuilder();
        ret.append("Some classes you could take are: ");
        for (int i = 0; i < Math.min(choices.size() - 2, 5); i++) {
            ret.append(choices.get(i).getCode());
            ret.append(", ");
        }
        ret.append("and ");
        ret.append(choices.get(Math.min(choices.size() - 1, 6)).getCode());
        ret.append(".");
        return ret.toString();
    }

}

class ClassNode {

    private String name;
    private String code;
    private List<ClassNode> children;
    private String UUID;
    private boolean isTaken;
    private int inDegree;
    private int outDegree;

    /**
     * Makes a new ClassNode object.
     * @param name name of the class.
     * @param code code for the class (i.e. NETS1500).
     * @param children a list of classes for which this class is a prerequisite for.
     */
    public ClassNode(String name, String code, String UUID, ClassNode... children) {
        this.name = name;
        this.code = code;
        this.children = new ArrayList<>(Arrays.asList(children));
        this.UUID = UUID;
        this.inDegree = 0;
        this.outDegree = 0;
        this.isTaken = false;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setCode(String newCode) {
        this.code = newCode;
    }
    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public List<ClassNode> getChildren() {
        return children;
    }

    public void addChild(ClassNode child) {
        this.children.add(child);
    }

    public void removeChild(ClassNode child) {
        this.children.remove(child);
    }

    public String getUUID() {
        return UUID;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public int getInDegree() {
        return inDegree;
    }

    public void setInDegree(int inDegree) {
        this.inDegree = inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public void setOutDegree(int outDegree) {
        this.outDegree = outDegree;
    }

    public int getThousand() {
        if (getCode() == null) {
            return -1;
        }
        return Integer.parseInt((getCode().substring(
                getCode().indexOf("\u00A0") + 1
        ))) / 1000;
    }

    public int getCodeAsInt() {
        return Integer.parseInt((getCode().substring(
                getCode().indexOf("\u00A0") + 1
        )));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassNode classNode = (ClassNode) o;
        return Objects.equals(getName(), classNode.getName())
                && Objects.equals(getCode(), classNode.getCode())
                && Objects.equals(getChildren(), classNode.getChildren());
    }

    @Override
    public String toString() {
        return this.code;
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(getName(), getCode(), getChildren());
//    }
}
