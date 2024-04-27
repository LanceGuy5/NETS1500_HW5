import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        return containsValHelper(root, code);
    }

    private boolean containsValHelper(ClassNode node, String code) {
        if (node.getCode().equals(code)) {
            return true;
        }
        for (ClassNode child : node.getChildren()) {
            if (containsValHelper(child, code)) {
                return true;
            }
        }
        return false;
    }

}

class ClassNode {

    private final String name;
    private final String code;
    private final List<ClassNode> children;

    /**
     * Makes a new ClassNode object.
     * @param name name of the class.
     * @param code code for the class (i.e. NETS1500).
     * @param children a list of classes for which this class is a prerequisite for.
     */
    public ClassNode(String name, String code, ClassNode... children) {
        this.name = name;
        this.code = code;
        this.children = new ArrayList<>(Arrays.asList(children));
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
    public int hashCode() {
        return Objects.hash(getName(), getCode(), getChildren());
    }
}
