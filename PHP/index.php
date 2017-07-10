<?php
header('Content-Type: text/html; charset=utf-8');

if (isset($_POST['tag']) && $_POST['tag'] != '')
{
	// get tag
	$tag = $_POST['tag'];
	
	// include db handler
	require_once 'include/DBFunctions.php';
	$db = new DBFunctions();
	
	// response Array
	$response = array("tag" => $tag, "error" => FALSE);
	
	// check for tag type
	switch ($tag)
	{
		case 'login':
		// Request type is check Login
		{
			// receiving the post params
			$email = $_POST['email'];
			$password = $_POST['password'];

			// check for user
			$user = $db->getUserByEmailAndPassword($email, $password);
			if($user != false)
			// user found
			{
				$response["error"] = FALSE;
				$response["user"]["user_type"] = $user["user_type"];
				$response["user"]["full_name"] = $user["full_name"];
				$response["user"]["birth_date"] = $user["birth_date"];
				$response["user"]["id_card"] = $user["id_card"];
				$response["user"]["vip_pref"] = $user["vip_pref"];
				$response["user"]["email"] = $user["email"];
				$response["user"]["created_at"] = $user["created_at"];
				$response["user"]["updated_at"] = $user["updated_at"];
				echo json_encode($response);
			}
			else
			// user is not found with the credentials
			{
				$response["error"] = TRUE;
				$response["error_msg"] = "Incorrect email or password";
				echo json_encode($response);
			}
			break;
		}			
		case 'register':
		{
			// receiving the post params
			$full_name = $_POST['full_name'];
			$birth_date = $_POST['birth_date'];
			$id_card = $_POST['id_card'];
			$vip_pref = $_POST['vip_pref'];
			$email = $_POST['email'];
			$password = $_POST['password'];

			// check if user is already existed with the same email
			if($db->isUserExisted($email))
			{
				// user is already existed - error response
				$response["error"] = TRUE;
				$response["error_msg"] = "User already existed with " . $email;
				echo json_encode($response);
			}
			else
			{		
				// create a new user
				$user = $db->storeUser($full_name, $birth_date, $id_card, $vip_pref, $email, $password);
				if($user)
				{
					// user stored successfully
					$response["error"] = FALSE;
					$response["user"]["full_name"] = $user["full_name"];
					$response["user"]["birth_date"] = $user["birth_date"];
					$response["user"]["id_card"] = $user["id_card"];
					$response["user"]["vip_pref"] = $user["vip_pref"];
					$response["user"]["email"] = $user["email"];
					$response["user"]["created_at"] = $user["created_at"];
					$response["user"]["updated_at"] = $user["updated_at"];
					echo json_encode($response);
				}
				else
				{
					// user failed to store
					$response["error"] = TRUE;
					$response["error_msg"] = "Error occured in Registration";
					echo json_encode($response);
				}
			}
			break;
		}			
		default:
		// Request type is unknown
		{
			$response["error"] = TRUE;
			$response["error_msg"] = "Unknown 'tag' value";
			echo json_encode($response);
		}
	}
}
else
{
	// tag is missing
	$response["error"] = TRUE;
	$response["error_msg"] = "Required  parameter 'tag' is missing";
	echo json_encode($response);
	
	/*
	// Testing
	require_once 'include/DBFunctions.php';
	$db = new DBFunctions();
	
	// response Array
	$response = array("tag" => $tag, "error" => FALSE);
	*/	
}

?>