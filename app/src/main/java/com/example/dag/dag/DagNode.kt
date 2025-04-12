package com.example.dag.dag

/**
 * DAG节点，代表流程中的一个步骤或任务
 */
class DagNode<T>(
    val id: String,
    val data: T,
    val dependencies: MutableSet<DagNode<T>> = mutableSetOf(),
    private var conditionCheck: (() -> Boolean)? = null,
    initBlock: (DagNode<T>.() -> Unit)? = null
) {
    init {
        initBlock?.invoke(this)
    }
    
    // 添加依赖节点
    fun dependsOn(node: DagNode<T>) {
        dependencies.add(node)
    }
    
    // 添加多个依赖节点
    fun dependsOn(vararg nodes: DagNode<T>) {
        nodes.forEach { dependencies.add(it) }
    }
    
    // 使用 + 运算符添加依赖
    operator fun DagNode<T>.unaryPlus() {
        this@DagNode.dependencies.add(this)
    }
    
    // 设置条件检查函数
    fun condition(
        check: () -> Boolean) {
        this.conditionCheck = check
    }
    
    // 检查节点条件是否满足
    fun isConditionMet(): Boolean {
        return conditionCheck?.invoke() != false and dependencies.all { it.isConditionMet() }
    }
    
    // 检查是否依赖于指定节点
    fun isDependentOn(node: DagNode<T>): Boolean {
        if (dependencies.contains(node)) return true
        
        // 递归检查间接依赖
        for (dependency in dependencies) {
            if (dependency.isDependentOn(node)) return true
        }
        
        return false
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DagNode<*>) return false
        return id == other.id
    }
    
    override fun hashCode(): Int {
        return id.hashCode()
    }

    // 打印依赖关系树
    fun printDependencyTree(indent: String = "", isLast: Boolean = true, visited: MutableSet<String> = mutableSetOf()): String {
        if (visited.contains(id)) {
            return "$indent${if (isLast) "└── " else "├── "}$id (循环依赖)\n"
        }

        visited.add(id)
        val currentIndent = "$indent${if (isLast) "└── " else "├── "}"
        val childIndent = "$indent${if (isLast) "    " else "│   "}"

        val builder = StringBuilder()
        builder.append("$currentIndent$id (${data})")

        // 添加条件状态
        val conditionStatus = if (conditionCheck?.invoke() == false) " ❌" else " ✓"
        builder.append("$conditionStatus\n")

        // 递归打印所有依赖
        val dependencyList = dependencies.toList()
        dependencyList.forEachIndexed { index, dependency ->
            val isLastDependency = index == dependencyList.size - 1
            builder.append(dependency.printDependencyTree(childIndent, isLastDependency, visited.toMutableSet()))
        }

        return builder.toString()
    }
}