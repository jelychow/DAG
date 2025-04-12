package com.example.dag.dag

import android.util.Log
import androidx.navigation.NavController
import com.example.dag.navigation.Screen
import com.example.dag.navigation.Screen.Main
import com.example.dag.utils.UserPreferences

/**
 * 应用流程管理器，使用DAG管理应用的导航流程
 */
class FlowManager(private val userPreferences: UserPreferences) {
    

    
    // 创建DAG管理器
    private val dagManager = DagManager<String>()
    
    init {
        // 创建流程节点，并添加条件检查
        val privacyNode = DagNode("privacy", Screen.PrivacyPolicy.route) {
            condition { userPreferences.hasAgreedToPrivacyPolicy() }
        }

        val loginNode = DagNode("login", Screen.Login.route) {
            +privacyNode  // 登录依赖于隐私政策同意
            condition { userPreferences.isLoggedIn() }
        }
        
        val mainNode = DagNode("main", Screen.Main.route) {
            +loginNode  // 主界面依赖于登录
            // 设置条件检查，始终返回false，表示没有条件检查，不需要自动跳转其他页面
            condition { false }
        }
        
        dagManager.dag {
            +privacyNode
            +loginNode
            +mainNode
        }

        Log.d("dagManager",dagManager.printDag())
    }


    
    // 获取当前应该显示的流程步骤
    fun getCurrentStep(): String {
        // 获取所有节点，按照拓扑排序
        val node: DagNode<String>? = dagManager.getStartNode()
        return node?.data?:Main.route
    }
    
    // 检查是否可以跳转到指定步骤
    fun canNavigateTo(step: String): Boolean {
        val targetNode = dagManager.getAllNodes().find { it.data == step } ?: return false
        
        // 检查所有依赖是否满足条件
        for (dependency in targetNode.dependencies) {
            if (!dependency.isConditionMet()) {
                return false
            }
        }

        return true
    }
    
    // 封装导航到下一步的逻辑
    fun navToNext(navController: NavController): Boolean {

        val node: DagNode<String>? = dagManager.getStartNode()

        navController.navigate(node?.data?:Main.route)
        
        return true
    }

}