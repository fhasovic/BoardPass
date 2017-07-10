<?php

class DBFunctions
{
	
	private $conn;
	
	// constructor
	function __construct()
	{
		require_once 'DBConnect.php';
		
		// connecting to database
		$db = new DBConnect();
		$this->conn = $db->connect();
	}
	
	// destructor
	function __destruct()
	{
		
	}
	
	/**
     * Storing new user
     * returns user details
     */
	public function storeUser($full_name, $birth_date, $id_card, $vip_pref,
							  $email, $password)
	{
		$hash = $this->hashBCRYPT($password);
		
		$stmt = $this->conn->prepare("INSERT INTO users(full_name, birth_date, id_card, vip_pref, email, encrypted_password, created_at)  VALUES(?, ?, ?, ?, ?, ?, NOW())");
		$stmt->bind_param("ssssss", $full_name, $birth_date, $id_card, $vip_pref, $email, $hash);
		$result = $stmt->execute();
		$stmt->close();
		
		// check for successful store
		if ($result)
		{
			$stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
			$stmt->bind_param("s", $email);	
			$stmt->execute();
			$user = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			
			return $user;
		}
		else
		{
			return false;
		}
	}

	/**
     * Get user by email and password
     */
	public function getUserByEmailAndPassword($email, $password)
	{
		// prepare and bind
		$stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
		$stmt->bind_param("s", $email);	
	
		if ($stmt->execute())
		{
			$user = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			
			// verifying user password
			$hash = $user['encrypted_password'];
			if ($this->VerifyPassword($password, $hash)) 
			{
				// user authentication details are correct
				return $user;
			}
		}
		else
		{
			return Null;
		}
	}

	/**
     * Check user is existed or not
     */
	public function isUserExisted($email)
	{
		// prepare and bind
		$stmt = $this->conn->prepare("SELECT email FROM users WHERE email = ?");
		$stmt->bind_param("s", $email);	
		$stmt->execute();
		$stmt->store_result();
		
		if ($stmt->num_rows > 0)
		{
			// user existed
			$stmt->close();
			return true;
		}
		else
		{
			// user not existed
			$stmt->close();
			return false;
		}
	}
	
	/**
     * Encrypting password with default bcrypt algorithm
     * @param password
     * returns encrypted password+salt (salt is part of the hash)
     */
	public function hashBCRYPT($password)
	{
		$hash = password_hash($password, PASSWORD_DEFAULT);
		return $hash;
	}

	/**
     * Verifying password with hash value
     * @param password, hash
     * returns true if password verified, else return false
     */
	public function VerifyPassword($password, $hash)
	{
		if (password_verify($password, $hash)) 
		{
			// Success!
			return True;
		}
		else 
		{
			// Invalid credentials
			return False;
		}
	}
}

?>