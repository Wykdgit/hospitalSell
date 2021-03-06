<html>
    <#include "../common/header.ftl">
    <body>
    <div id="wrapper" class="toggled">
        <#--边栏sidebar-->
        <#include "../common/nav.ftl">
        <#--主要内容-->
        <div id="page-content-wrapper">
            <div class="container-fluid">
                <div class="row clearfix">
                    <div class="col-md-12 column">
                        <div class="row clearfix">
                            <div class="col-md-12 column">
                            <#--商品概要表-->
                                <table class="table table-bordered table-condensed">
                                    <thead>
                                    <tr>
                                        <th>订单id</th>
                                        <th>姓名</th>
                                        <th>手机号</th>
                                        <th>地址</th>
                                        <th>金额</th>
                                        <th>订单状态</th>
                                        <th>支付状态</th>
                                        <th>创建时间</th>
                                        <th colspan="2">操作</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                            <#list orderDtoPage.getContent() as orderDto>
                            <tr>
                                <td><a href="/seller/order/detail?orderId=${orderDto.orderId}">${orderDto.orderId}</a></td>
                                <td>${orderDto.userName}</td>
                                <td>${orderDto.userPhone}</td>
                                <td>${orderDto.userAddress}</td>
                                <td>${orderDto.orderAmount}</td>
                                <td>${orderDto.getOrderStatusEnum().message}</td>
                                <td>${orderDto.getPayStatusEnum().message}</td>
                                <td>${orderDto.createTime}</td>
                                <td>
                                <#--当订单为新下单状态时才显示接单操作-->
                                    <#if orderDto.orderStatus == 0>
                                    <a href="/seller/order/finish?orderId=${orderDto.orderId}">接单</a>
                                    </#if>
                                </td>
                                <td>
                                <#--当订单为新下单状态时才显示取消操作-->
                                    <#if orderDto.orderStatus == 0>
                                        <a href="/seller/order/cancel?orderId=${orderDto.orderId}">取消</a>
                                    </#if>
                                </td>
                            </tr>
                            </#list>
                                    </tbody>
                                </table>
                                <div>
                                    <p><b>备注：</b>点击订单号，可查看订单详情</p>
                                </div>
                            <#--分页-->
                                <ul class="pagination">
                                <#--lte表示小于等于，gte表示大于等于-->
                            <#if currentPage lte 1>
                                <li class="disabled"><a href="#">上一页</a></li>
                            <#else>
                                <li><a href="/seller/order/list?page=${currentPage-1}&size=${size}">上一页</a></li>
                            </#if>
                                <#--0..orderDtoPage.getTotalPages()表示一个0到orderDtoPage.getTotalPages()的list-->
                            <#list 1..orderDtoPage.getTotalPages() as index>
                                <#if currentPage == index>
                                    <li class="disabled"><a href="#">${index}</a></li>
                                <#else>
                                    <li><a href="/seller/order/list?page=${index}&size=${size}">${index}</a></li>
                                </#if>
                            </#list>
                                <#--lte表示小于等于，gte表示大于等于-->
                            <#if currentPage gte orderDtoPage.getTotalPages()>
                                <li class="disabled"><a href="#">下一页</a></li>
                            <#else>
                                <li><a href="/seller/order/list?page=${currentPage+1}&size=${size}">下一页</a></li>
                            </#if>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <#--播放音乐-->
    <audio id="notice" loop="loop">
        <source src="/mp3/song.mp3" type="audio/mpeg" />
    </audio>
    <#--引入jquery和bootstrap-js-->
    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <#--webSocket事件-->
    <script>
        var webSocket = null;
        if('WebSocket' in window){
            //如果该浏览器支持webSocket,则创建weSocket，注意这里不是http协议
            webSocket = new WebSocket('ws://cx.s1.natapp.cc/sell/webSocket');
        }
        else {
            alert('该浏览器不支持webSocket');
        }

        webSocket.onopen = function (event) {
            console.log('建立连接');
        }
        webSocket.onclose = function (event) {
            console.log('关闭连接')
        }
        webSocket.onmessage = function (event) {
            console.log('收到消息：'+event.data);
            //弹窗提醒 并 播放音乐
            $('#myModal').modal('show');
            document.getElementById('notice').play();
        }
        webSocket.onerror = function (event) {
            alert('webSocket发生错误！');
        }
        webSocket.onbeforeunload = function (event) {
            //当窗口关闭时，关闭webSocket
            webSocket.close();
        }
    </script>
    </body>
</html>