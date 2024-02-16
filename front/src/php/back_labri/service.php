
<?php

if(file_exists("request_backend.php"))
    include_once("request_backend.php");
else {
    error_log("back_labri: missing request_backend.php");
    http_response_code(500);
}


header('Content-Type: text/html; charset ="UTF-8"');

    $input = $_POST;
    $aResult = array();

    if( !isset($input['serviceName']) ) {
        http_response_code(500);
    	echo 'back_labri: No service name provided.';
    } else if( !isset($input['data']) ) {
        http_response_code(500);
    	echo 'back_labri: No data provided.';
    } else {
        try {
            echo request_backend($input['serviceName'], $input['data']);
        } catch (Exception $e) {
            http_response_code(400);
            echo $e->getMessage();

        } catch (Error $e) {
            http_response_code(500);
            echo $e->getMessage();
        }

    }

    

?>
