<?php

	function Exchange_Currency($from, $to, $amount)
	{
		$VND_Currency = array( 'USD' => 22871.0);
		$USD_Currency = array( 'VND' => 0.000044);
									
		if($from == 'VND')
		{
			return $amount / $VND_Currency[$to];

		}else if($from == 'USD')
		{
			return $amount * $USD_Currency[$to];
		}
	}

?>