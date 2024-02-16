<?php

if(file_exists("settings.php"))
    include_once("settings.php");
else {
    error_log("missing settings.php");
    http_response_code(500);
}

function get_post_data ()
{
    if (!isset ($_POST)) {
        throw new ErrorException("back_labri: Bad request");
    }

    $inputdata = file_get_contents("php://input");
    if (empty($inputdata)) {
        throw new ErrorException("back_labri: Empty request");
    }
    return $inputdata;
}

function httpPost($url, $data)
{

    $options = array(
    'http' => array(
        'header'  => 'Content-type: application/json\r\n',
        'method'  => 'POST',
        'content' => $data
    )
    );
    $context  = stream_context_create($options);
    $response = file_get_contents($url, false, $context);
    if ($response === false)
        throw new ErrorException("back_labri: stream context returned empty stream");

    return $response;
}

function request_backend($service_name, $data)
{
    global $BACKEND_URL;
    if (! isset($BACKEND_URL)) {
       throw new ErrorException("back_labri: unknown backend url.");
    }
    $url = $BACKEND_URL.$service_name;
    $answer = httpPost($url, $data);
    return $answer;
}
?>
