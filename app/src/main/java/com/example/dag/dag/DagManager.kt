package com.example.dag.dag

/**
 * DAG管理器，负责管理节点和执行流程
 */
class DagManager<T> {
    private val nodes = mutableMapOf<String, DagNode<T>>()

    // 添加节点
    fun addNode(node: DagNode<T>) {
        // 检查是否会形成循环依赖
        for (existingNode in nodes.values) {
            require(!(node.isDependentOn(existingNode) && existingNode.isDependentOn(node))) {
                "添加节点 ${node.id} 会导致循环依赖"
            }
        }
        nodes[node.id] = node
    }

    // 使用 + 运算符添加节点
    operator fun plus(node: DagNode<T>): DagManager<T> {
        addNode(node)
        return this
    }

    operator fun DagNode<T>.unaryPlus(): DagManager<T> {
        this@DagManager.addNode(this)
        return this@DagManager
    }

    fun dag(block: DagManager<T>.() -> Unit) {
        block()
    }

    // 使用 += 运算符添加节点
    operator fun plusAssign(node: DagNode<T>) {
        addNode(node)
    }

    // 获取节点
    fun getNode(id: String): DagNode<T>? = nodes[id]

    // 获取所有节点
    fun getAllNodes(): List<DagNode<T>> = nodes.values.toList()

    // 获取指定节点的所有依赖节点
    fun getDependencies(nodeId: String): List<DagNode<T>> {
        return nodes[nodeId]?.dependencies?.toList() ?: emptyList()
    }

    // 获取依赖于指定节点的所有节点
    fun getDependents(nodeId: String): List<DagNode<T>> {
        val node = nodes[nodeId] ?: return emptyList()
        return nodes.values.filter { it.dependencies.contains(node) }
    }

    /**
     * 获取拓扑排序的节点列表（基于入度的Kahn算法）
     * 从入度为0的节点开始，逐步删除节点及其出边，更新其他节点的入度
     */
    fun getStartNodes(): List<DagNode<T>> {
        val inDegree = mutableMapOf<String, Int>()
        val nodeMap = nodes.values.associateBy { it.id }

        // Calculate in-degrees
        for (node in nodes.values) {
            inDegree[node.id] = node.dependencies.size
        }

        // Initialize queue with nodes having in-degree 0
        val queue = ArrayDeque<DagNode<T>>()
        for ((id, degree) in inDegree) {
            if (degree == 0) {
                nodeMap[id]?.let { queue.add(it) }
            }
        }

        val result = mutableListOf<DagNode<T>>()

        // Process nodes in topological order
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            result.add(node)

            for (dependent in getDependents(node.id)) {
                inDegree[dependent.id] = inDegree.getOrDefault(dependent.id, 0) - 1
                if (inDegree[dependent.id] == 0) {
                    queue.add(dependent)
                }
            }
        }

        // Check for circular dependencies
        if (result.size != nodes.size) {
            val cycleNodes = nodes.values.filterNot { it in result }.joinToString(", ") { it.id }
            throw IllegalStateException("检测到循环依赖，以下节点涉及循环: $cycleNodes")
        }

        return result
    }

    /**
     * 获取第一个可执行的节点（条件满足且所有依赖都满足条件的节点）
     */
    fun getStartNode(): DagNode<T>? {
        val sortedNodes = getStartNodes()

        for (node in sortedNodes) {
            // 检查节点自身条件
            if (!node.isConditionMet()) {
                return node
            }

            // 检查所有依赖节点的条件
            val allDependenciesMet = node.dependencies.all { it.isConditionMet() }
            if (!allDependenciesMet) {
                // 找到第一个不满足条件的依赖节点
                val firstUnmetDependency = node.dependencies.firstOrNull { !it.isConditionMet() }
                return firstUnmetDependency
            }
        }
        // 如果所有节点条件都满足，返回最后一个节点（终点）
        return sortedNodes.lastOrNull()
    }


    /**
     * 打印整个DAG的依赖关系
     */
    fun printDag(): String {
        val builder = StringBuilder()
        builder.append("DAG依赖关系图:\n")
        
        // 获取所有没有被依赖的节点（终端节点）
        val terminalNodes = nodes.values.filter { node ->
            nodes.values.none { it.dependencies.contains(node) }
        }
        
        // 从终端节点开始打印
        terminalNodes.forEachIndexed { index, node ->
            builder.append(node.printDependencyTree(isLast = index == terminalNodes.size - 1))
        }
        
        return builder.toString()
    }
}