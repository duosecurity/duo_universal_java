<html>
    <head>
        <link rel="stylesheet" href='style.css'>
    </head>
    <body>
        <div class="content">
            <div class="logo">
                <img src="logo.png">
            </div>
            <div class="output">
                <p>${message}</p>
            </div>
            <form class="input-form" action="/" method="POST">
                <div class="form-group">
                    <label for="exampleInputEmail1">Name:</label>
                    <input type="text" name="username" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp">
                </div>
                <div class="form-group">
                    <label for="exampleInputPassword1">Password:</label>
                    <input type="password" name="password" class="form-control" id="exampleInputPassword1">
                </div>
                <div class="buttons">
                    <button type="submit" class="btn btn-primary">Submit</button>
                </div>
            </form>
        </div>
    </body>
</html>
