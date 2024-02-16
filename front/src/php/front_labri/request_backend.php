<?php

if(file_exists("settings.php"))
    include_once("settings.php");
else {
    error_log("front_labri: missing settings.php");
    http_response_code(500);
}

function httpPost($url, $data)
{
    $curl = curl_init($url);
    curl_setopt($curl, CURLOPT_POST, true);
    curl_setopt($curl, CURLOPT_POSTFIELDS, $data);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($curl, CURLOPT_TIMEOUT, 2);
    curl_setopt($curl, CURLOPT_CONNECTTIMEOUT, 2);
     $response = curl_exec($curl);
    curl_close($curl);
     if ($response === false)
        throw new ErrorException(curl_error($curl));
    return $response;
}

function request_backend($service_name, $data)
{
    global $BACKEND_URL;
    if (! isset($BACKEND_URL)) {
       throw new ErrorException("front_labri: unknown backend url.");
    }
    try {
        /*
        if ($service_name == "email") {
            $ddata = json_decode($data);
            $to = $ddata['to'];
            $subject = $ddata['subject'];
            $body = $ddata['body'];
            $result = mail(
            filter_var($to, FILTER_SANITIZE_EMAIL),
            filter_var($subject, FILTER_SANITIZE_STRING),
            filter_var($body, FILTER_SANITIZE_STRING),
            )
            return "Envoi email ".$data." résultat ".$result;
        } else {
            $url = $BACKEND_URL.$service_name;
            return httpPost($url, $data);
        }*/
        $url = $BACKEND_URL.$service_name;
        return httpPost($url, $data);
    } catch(Exception $e) {
        $url = $BACKEND_URL2.$service_name;
        return httpPost($url, $data);
    }
}
?>
