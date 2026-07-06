<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>文件上传 - Scenario 05</title>
    <link rel="stylesheet" href="/css/common.css"/>
</head>
<body>
<div class="container narrow">
    <h1>文件上传 <span class="tag">Scenario 05</span></h1>
    <form action="/scenario05/file/upload" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <label>选择文件</label>
            <input type="file" name="file" required/>
        </div>
        <div class="form-group">
            <label>描述</label>
            <input type="text" name="description"/>
        </div>
        <button type="submit">上传</button>
    </form>
</div>
</body>
</html>
