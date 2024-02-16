
<?php

    include_once("request_backend.php");

    header('Content-Type: application/json');

    $input = $_POST;

    try {
	    echo request_backend('',$input);
	} catch (Exception $e) {
        http_response_code(400);
        echo 'parcoursup.labri.fr: request_backend threw an exception. '.$e->getMessage();
    } catch (Error $e) {
        http_response_code(500);
        echo 'parcoursup.labri.fr: request_backend threw an error. '.$e->getMessage();
    }

?>