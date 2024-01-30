package com.chinamobile.entity.node;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    private T value;
    private List<TreeNode<T>> children;

    public TreeNode(T value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void addChild(TreeNode<T> child) {
        children.add(child);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    // 可选的 toString 方法，用于打印树形结构
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        print("", true, sb);
        return sb.toString();
    }

    private void print(String prefix, boolean isTail, StringBuilder sb) {
        sb.append(prefix).append(isTail ? "└── " : "├── ").append(value).append("\n");
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "    " : "│   "), false, sb);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1)
                    .print(prefix + (isTail ?"    " : "│   "), true, sb);
        }
    }


    //TreeNode<Integer> root = new TreeNode<>(1);
    //root.addChild(new TreeNode<>(2));
    //root.addChild(new TreeNode<>(3));
    //root.getChildren().get(0).addChild(new TreeNode<>(4));
    //root.getChildren().get(0).addChild(new TreeNode<>(5));

}
