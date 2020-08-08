package restdoc.util.tmp;

import restdoc.model.FieldType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreeNode {

    private String name;

    private TreeNode parent;

    private Object value;

    private FieldType type;

    private List<TreeNode> children;

    private Map<String, Integer> childrenMap;

    private boolean isRoot;

    static private final Pattern pattern = Pattern.compile("\\[\\]?(.*)\\[\\]?");

    public TreeNode(String name, TreeNode parent) {
        this.name = name;
        if (this.name == null) {
            this.name = "root";
        }
        this.parent = parent;
        this.children = new LinkedList<>();
        this.childrenMap = new HashMap<>();
        this.isRoot = parent == null;
    }

    public String getName() {
        return name;
    }

    public TreeNode getParent() {
        return parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    /**
     */
    public TreeNode addChild(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        int i = name.indexOf("."), len = name.length();
        String currNodeName = i >= 0 ? name.substring(0, i) : name,
                childrenNodeName = i >= 0 ? name.substring(i + 1, len) : null;

        if (currNodeName == null || currNodeName.length() == 0) {
            return null;
        }
        if (this.isRoot) {
            Matcher matcher = pattern.matcher(currNodeName);
            if (!matcher.matches()) {
                return null;
            }
            currNodeName = matcher.group(1);
        }
        TreeNode currNode = this.getChild(currNodeName);
        if (currNode == null) {
            currNode = new TreeNode(currNodeName, this);
            this.children.add(currNode);
            this.childrenMap.put(currNodeName, this.children.size() - 1);
        }
        if (childrenNodeName == null || childrenNodeName.length() == 0) {
            return currNode;
        }
        return currNode.addChild(childrenNodeName);
    }

    /**
     *
     *
     *  [[]]
     * 查找当前节点中是否有目标节点，仅支持子节点，不支持后代节点
     */
    public TreeNode getChild(String childNodeName) {
        if (!containsNode(childNodeName)) {
            return null;
        }
        return children.get(childrenMap.get(childNodeName));
    }

    /**
     * 从根节点搜索起
     */
    public boolean containsNode(String name) {
        if (name == null || name.length() == 0) {
            return true;
        }
        return this.childrenMap.containsKey(name);
    }

    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "name='" + name + '\'' +
                ", parent=" + parent +
                ", children=" + children +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeNode treeNode = (TreeNode) o;

        return name != null ? name.equals(treeNode.name) : treeNode.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
