package com.zhangteng.treerecyclerview.tree;

import com.zhangteng.treerecyclerview.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TreeHelper {
    /**
     * 传入我们的普通bean，转化为排序后的Node
     *
     * @param datas
     * @param defaultExpandLevel
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> List<Node> getSortedNodes(List<T> datas, int defaultExpandLevel) throws IllegalArgumentException, IllegalAccessException {
        List<Node> result = new ArrayList<Node>();
        // 将用户数据转化为List<Node>
        List<Node> nodes = convetData2Node(datas);
        // 拿到根节点
        List<Node> rootNodes = getRootNodes(nodes);
        // 排序以及设置Node间关系
        for (Node node : rootNodes) {
            addNode(result, node, defaultExpandLevel, 1);
        }
        return result;
    }

    /**
     * 过滤出所有可见的Node
     *
     * @param nodes
     * @return
     */
    public static List<Node> filterVisibleNode(List<Node> nodes) {
        List<Node> result = new ArrayList<Node>();
        for (Node node : nodes) {
            // 如果为跟节点，或者上层目录为展开状态
            if (node.isRoot() || node.isParentExpand()) {
                setNodeIcon(node);
                result.add(node);
            }
        }
        return result;
    }

    /**
     * 将我们的数据转化为树的节点
     *
     * @param datas
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private static <T> List<Node> convetData2Node(List<T> datas) throws IllegalArgumentException, IllegalAccessException {
        List<Node> nodes = new ArrayList<Node>();
        List<Node> nodeChildren = new ArrayList<Node>();
        Node node = null;

        for (T t : datas) {
            String id = null;
            String pId = null;
            String label = null;
            List<T> children = new ArrayList<>();
            Class<? extends Object> clazz = t.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : declaredFields) {
                if (f.getAnnotation(TreeNodeId.class) != null) {
                    f.setAccessible(true);
                    id = (String) f.get(t);
                }
                if (f.getAnnotation(TreeNodePid.class) != null) {
                    f.setAccessible(true);
                    pId = (String) f.get(t);
                }
                if (f.getAnnotation(TreeNodeLabel.class) != null) {
                    f.setAccessible(true);
                    label = (String) f.get(t);
                }
                if (f.getAnnotation(TreeNodeChildren.class) != null) {
                    f.setAccessible(true);
                    children = (List<T>) f.get(t);
                    if (children != null) {
                        nodeChildren = convetData2Node(children);
                    }
                }
                if (id != null && pId != null && label != null) {
                    break;
                }
            }
            if (pId == null) {
                pId = "0";
            }
            node = new Node(id, pId, label);
            for (Node child : nodeChildren) {
                if (child.getParent() == null) {
                    child.setParent(node);
                    node.getChildren().add(child);
                }
            }
            nodes.addAll(nodeChildren);
            nodes.add(node);
        }

        // 设置图片
        for (Node n : nodes) {
            setNodeIcon(n);
        }
        return nodes;
    }

    private static List<Node> getRootNodes(List<Node> nodes) {
        List<Node> root = new ArrayList<Node>();
        for (Node node : nodes) {
            if (node.isRoot()) {
                root.add(node);
            }
        }
        return root;
    }

    /**
     * 把一个节点上的所有的内容都挂上去
     */
    private static void addNode(List<Node> nodes, Node node,
                                int defaultExpandLeval, int currentLevel) {

        nodes.add(node);
        //设置默认展开
        /*if (defaultExpandLeval >= currentLevel) {
            node.setExpand(true);
        }*/

        if (node.isLeaf()) {
            return;
        }
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(nodes, (Node) node.getChildren().get(i), defaultExpandLeval,
                    currentLevel + 1);
        }
    }

    /**
     * 设置节点的图标
     *
     * @param node
     */
    private static void setNodeIcon(Node node) {
        if (node.getChildren().size() > 0 && node.isExpand()) {
            node.setIcon(R.mipmap.ic_more_bottom_gray);
        } else if (node.getChildren().size() > 0 && !node.isExpand()) {
            node.setIcon(R.mipmap.ic_more_right_gray);
        } else {
            node.setIcon(-1);
        }

    }

}