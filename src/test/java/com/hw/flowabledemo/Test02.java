package com.hw.flowabledemo;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class Test02 {
    ProcessEngineConfiguration configuration = null;

    @BeforeEach
    public void before() {
        //获取ProcessEngineConfiguration对象
        configuration = new StandaloneProcessEngineConfiguration();

        //配置数据库连接信息
        configuration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        //nullCatalogMeansCurrent=true防止创表出错
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/flowable_demo?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("123456");

        //数据库表不存在就创建
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
    }

    /**
     * 部署流程
     * 执行完以后会在数据库生产3条记录：表分别是：act_ge_bytearry、act_re_deployment、act_re_procdef
     */
    @Test
    public void testDeploy() {
        //1.获取ProcessEngine对象
        ProcessEngine processEngine = configuration.buildProcessEngine();
        //2.获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.完成流程的部署操作
        Deployment deploy = repositoryService.createDeployment()
                //关联resources下的holiday-request.bpmn20.xml流程文件
                .addClasspathResource("processes/MyHoliday.bpmn20.xml")
                //流程名称
                .name("Flowable-UI请假流程")
                .deploy();
        log.info("deploy.getId()是act_re_deployment表的Id：：{}", deploy.getId());
        log.info("deploy.getName()是act_re_deployment表的Name：{}", deploy.getName());
    }

    //=============================================================================================

    /**
     * 启动流程实例
     * 如果执行了testDeployDelete()，需要先执行完testDeploy()，再执行当前方法
     * 数据库表生产记录：act_ru_variable流程变量、act_ru_task流程任务、act_ru_execution流程信息
     */
    @Test
    public void testRunDeploy() {
        ProcessEngine processEngine = configuration.buildProcessEngine();
        //通过RuntimeService启动流程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //启动流程实例
        ProcessInstance holidayRequest = runtimeService.startProcessInstanceById("MyHoliday:2:40004");
        log.info("流程定义ID：holidayRequest.getProcessDefinitionId()：{}", holidayRequest.getProcessDefinitionId());
        log.info("流程部署(流程图)ID：holidayRequest.getDeploymentId()：{}", holidayRequest.getDeploymentId());
        log.info("活动流程ID：holidayRequest.getActivityId()：{}", holidayRequest.getActivityId());
        log.info("当前流程任务（一条流程任务）ID：holidayRequest.getId()：{}", holidayRequest.getId());
    }

    /**
     * 完成当前流程任务
     * act_ru_task会删除掉执行完的任务，act_ru_variable流程变量也会被清空。act_hi_actinst表生成流程活动记录，act_hi_varinst表生成流程变量
     */
    @Test
    public void testTaskComplete() {
        ProcessEngine processEngine = configuration.buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("MyHoliday")
//                .taskAssignee("admin")
//                .taskAssignee("root")
                //对应act_ru_task表的assignee_字段
                .taskAssignee("小李")
                .singleResult();
        //创建流程变量
        HashMap<String, Object> variables = new HashMap<>();
        //xml里定义的${approved}变量名称和值
        variables.put("approved", false);
        //完成任务
        taskService.complete(task.getId(), variables);
    }

    /**
     * 获取流程任务的历史数据
     */
    @Test
    public void testHistory() {
        ProcessEngine processEngine = configuration.buildProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                //流程定义ID
                .processDefinitionId("MyHoliday:1:30004")
                //流程状态：已完成
                .finished()
                //指定排序的字段和顺序
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        for (HistoricActivityInstance history : list) {
            log.info(history.getActivityName() + "：" + history.getAssignee() + "--" + history.getActivityId() + "：" + history.getDurationInMillis()+"毫秒");
        }
    }
}
