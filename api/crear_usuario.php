<?php
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Headers: *");
require_once("./conexion/conexion_bd.php");
 
$postdata = file_get_contents("php://input"); 
$request = json_decode($postdata);
$imagen = $request->url_foto;



$idImagen = uniqid();
    $folderPath = "./imagenes/";
    $image_parts = explode(";base64,", $imagen);
    $image_type_aux = explode("image/", $image_parts[0]);
    $image_type = $image_type_aux[1];
    $image_base64 = base64_decode($image_parts[1]);
    $file = $folderPath . $idImagen . '.png';
    file_put_contents($file, $image_base64);   
    $ruta = "http://167.99.158.191/examen/api/imagenes/". $idImagen . '.png';
    
$sql_query = "SELECT * FROM public.ft_crear_usuario('".$postdata."' , '".$ruta."')";

if (pg_send_query($con, $sql_query)) {
    $res=pg_get_result($con);
    if ($res) {
      $state = pg_result_error_field($res, PGSQL_DIAG_SQLSTATE);
      if ($state==0) {
            $datos = pg_fetch_all($res);
            $variable = (boolean)$datos[0]["ft_crear_usuario"];
            $resultado = array('estado' => $variable);
            echo json_encode($resultado) ;    
            }
      else {
        // some error happened
        if ($state == 23505) { // unique_violation
            $resultado = array('estado' => $state);
            echo json_encode($resultado) ;    
        }
        else {
            $resultado = array('estado' => $state);
            echo json_encode($resultado) ;
         // process other errors
        }
      }
    }  
  }

 
?>