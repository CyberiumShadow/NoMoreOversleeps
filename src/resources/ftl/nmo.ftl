<html>
<head>
<meta charset="utf-8">
<meta content="text/html; charset=UTF-8" http-equiv="content-type"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="cache-control" content="max-age=0" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
<meta http-equiv="pragma" content="no-cache" />
<title>NoMoreOversleeps</title>
<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.3/css/jasny-bootstrap.css" integrity="sha256-t0GDqKZnpVh1OVTSGzI7i52hYH9DN68z81MATIqktRo=" crossorigin="anonymous" />
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" integrity="sha384-wvfXpqpZZVQGK6TAh5PVlGOfQNHSoD2xbE+QkPxCAFlNEevoEH3Sl0sibVcOQVnN" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css" integrity="sha256-3iu9jgsy9TpTwXKb7bNQzqWekRX7pPK+2OLj3R922fo=" crossorigin="anonymous" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.2/css/bootstrap-select.min.css" integrity="sha256-an4uqLnVJ2flr7w0U74xiF4PJjO2N5Df91R2CUmCLCA=" crossorigin="anonymous" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ekko-lightbox/5.1.1/ekko-lightbox.min.css" integrity="sha256-8aNQFtmxcOMVoOhLD4mrHqaKC2Ui++LmlQsoKTqxwIE=" crossorigin="anonymous" />
<!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->
</head>
<body>
<div class="container">
<h1>NoMoreOversleeps WEB UI <small>v${version}</small> <a href="https://bitbucket.org/Tinytimrob/nomoreoversleeps"><button type="submit" class="btn-sm btn-primary" style="padding:2px; position:relative; top:-4px;">Download source</button></a></h1>
  <div style="clear:both; padding-top:4px;"></div>
  <h4>Status</h4>
  <table class="table table-striped">
    <tbody>
      <tr>
        <td><strong>Last refresh</strong></td>
        <td id="nmo_last_refresh">never</td>
      </tr>
      <tr>
        <td><strong>NoMoreOversleeps state</strong></td>
        <td id="nmo_pause_state">?</td>
      </tr>
      <tr>
        <td style="width:230px;"><strong>Last activity detected</strong></td>
        <td id="nmo_activity">?</td>
      </tr>
    </tbody>
  </table>
  <div style="float:right;"><img id="webcamImage" height=320/></div>
  <h4>Manual controls</h4>
  <form method="POST" action="/beep">
      <button type="submit" class="btn btn-success">BEEP</button>
  </form>
  <form method="POST" action="/vibration">
      <button type="submit" class="btn btn-warning">VIBRATE</button>
  </form>
  <form method="POST" action="/shock">
      <button type="submit" class="btn btn-danger">SHOCK</button>
  </form>
  <form method="POST" action="/call_switchboard">
      <button type="submit" class="btn btn-primary">CALL SWITCHBOARD: ${phoneSwitchboard}</button>
  </form>
  <form method="POST" action="/call_mobile">
      <button type="submit" class="btn btn-primary">CALL MOBILE: ${phoneMobile}</button>
  </form>
  <div style="clear:both;"></div>
  <h4>Log</h4>
  <iframe id="logframe" style="width:100%; height:300px" src="/log"></iframe>
</div>
<script src="https://code.jquery.com/jquery-2.1.4.min.js" integrity="sha384-R4/ztc4ZlRqWjqIuvf6RX5yb/v90qNGx6fS48N0tRxiGkqveZETq72KgDVJCp2TC" crossorigin="anonymous" type="text/javascript"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous" type="text/javascript"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.3/js/jasny-bootstrap.js" integrity="sha256-NG7ORCRPjgqoYm8LQoOuJjXtFW2++/ElxKFup4JwXS8=" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/1000hz-bootstrap-validator/0.11.9/validator.min.js" integrity="sha256-dHf/YjH1A4tewEsKUSmNnV05DDbfGN3g7NMq86xgGh8=" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.2/js/bootstrap-select.min.js" integrity="sha256-JD3g+rB9BjW6/cGEuwCue1sGtitb2aQVNs/pl4114XQ=" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/ekko-lightbox/5.1.1/ekko-lightbox.min.js" integrity="sha256-1odJPEl+KoMUaA1T7QNMGSSU/r5LCKCRC6SL8P0r2gY=" crossorigin="anonymous"></script>
<script>
    $(document).ready(function() {
        function request() {
            $.ajax({
                url: "/json",
                dataType: "text",
                cache: false,
                success: function(data) {
                    var json = $.parseJSON(data);
                    $('#nmo_last_refresh').html(json.update);
                    $('#nmo_activity').html(json.activity);
                    $('#nmo_pause_state').html(json.pause_state);
                    $('#logframe').attr('src',function(i,val){return val;});
                }
            });
        }
        request();
        setInterval(request, 1863);
    });
$(document).ready(function() {
	var ws = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/webcam");
	ws.onopen = function(e) {
		if (typeof console !== 'undefined') {
			console.info('WS open');
		}
	};
	ws.onmessage = function (e) {
		var data = JSON.parse(e.data), type = data.type, i = 0, $webcams = $('#webcams'), $img = null;
		if (typeof console !== 'undefined') {
			console.info('WS message', type);
		}
		$img = $('#webcamImage');
		$img.attr("src", "data:image/jpeg;base64," + data.image).addClass('shadow').trigger("change");
		setTimeout(function() {
			$img.removeClass('shadow').trigger("change");
		}, 1000);
	};
	ws.onclose = function() {
		if (typeof console !== 'undefined') {
			console.info('WS close');
		}
	};
	ws.onerror = function(err) {
		if (typeof console !== 'undefined') {
			console.info('WS error');
		}
	};
});
</script>
</body>
</html>