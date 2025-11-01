<?xml version='1.0' encoding='UTF-8'?>
<Project Type="Project" LVVersion="25008000">
	<Property Name="NI.LV.All.SaveVersion" Type="Str">25.0</Property>
	<Property Name="NI.LV.All.SourceOnly" Type="Bool">true</Property>
	<Item Name="My Computer" Type="My Computer">
		<Property Name="server.app.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.control.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.tcp.enabled" Type="Bool">false</Property>
		<Property Name="server.tcp.port" Type="Int">0</Property>
		<Property Name="server.tcp.serviceName" Type="Str">My Computer/VI Server</Property>
		<Property Name="server.tcp.serviceName.default" Type="Str">My Computer/VI Server</Property>
		<Property Name="server.vi.callsEnabled" Type="Bool">true</Property>
		<Property Name="server.vi.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="specify.custom.address" Type="Bool">false</Property>
		<Item Name="Controls" Type="Folder">
			<Item Name="Dependand_Variables.ctl" Type="VI" URL="../Controls/Dependand_Variables.ctl"/>
			<Item Name="Likert.Results.ctl" Type="VI" URL="../Controls/Likert.Results.ctl"/>
			<Item Name="Map.DontInterfere.ctl" Type="VI" URL="../Controls/Map.DontInterfere.ctl"/>
			<Item Name="Map.EasyBalance.ctl" Type="VI" URL="../Controls/Map.EasyBalance.ctl"/>
			<Item Name="Map.Enjoyment.ctl" Type="VI" URL="../Controls/Map.Enjoyment.ctl"/>
			<Item Name="Map.HowComfortable.ctl" Type="VI" URL="../Controls/Map.HowComfortable.ctl"/>
			<Item Name="Map.HowConfident.ctl" Type="VI" URL="../Controls/Map.HowConfident.ctl"/>
			<Item Name="Map.HowDistracting.ctl" Type="VI" URL="../Controls/Map.HowDistracting.ctl"/>
			<Item Name="Map.HowEasy.ctl" Type="VI" URL="../Controls/Map.HowEasy.ctl"/>
			<Item Name="Map.HowEffortless.ctl" Type="VI" URL="../Controls/Map.HowEffortless.ctl"/>
			<Item Name="Map.HowFocussed.ctl" Type="VI" URL="../Controls/Map.HowFocussed.ctl"/>
			<Item Name="Map.HowIntuitive.ctl" Type="VI" URL="../Controls/Map.HowIntuitive.ctl"/>
			<Item Name="Map.HowPrecisely.ctl" Type="VI" URL="../Controls/Map.HowPrecisely.ctl"/>
			<Item Name="Map.HowSafe.ctl" Type="VI" URL="../Controls/Map.HowSafe.ctl"/>
			<Item Name="Map.LookAway.ctl" Type="VI" URL="../Controls/Map.LookAway.ctl"/>
			<Item Name="State.ctl" Type="VI" URL="../Controls/State.ctl"/>
		</Item>
		<Item Name="SubVIs" Type="Folder">
			<Item Name="Calc dependand variables.vi" Type="VI" URL="../SubVIs/Calc dependand variables.vi"/>
			<Item Name="CountStringOccurrences.vi" Type="VI" URL="../SubVIs/CountStringOccurrences.vi"/>
			<Item Name="Filter_Table.vi" Type="VI" URL="../SubVIs/Filter_Table.vi"/>
			<Item Name="Get Participant ID and Input method.vi" Type="VI" URL="../SubVIs/Get Participant ID and Input method.vi"/>
			<Item Name="GetSafetyAndComfortLikerts.vi" Type="VI" URL="../SubVIs/GetSafetyAndComfortLikerts.vi"/>
			<Item Name="Unix time string to time stamp.vi" Type="VI" URL="../SubVIs/Unix time string to time stamp.vi"/>
		</Item>
		<Item Name="DataSummarizer.vi" Type="VI" URL="../DataSummarizer.vi"/>
		<Item Name="DataViewer.vi" Type="VI" URL="../DataViewer.vi"/>
		<Item Name="SurveyAnalyzation.vi" Type="VI" URL="../SurveyAnalyzation.vi"/>
		<Item Name="Dependencies" Type="Dependencies"/>
		<Item Name="Build Specifications" Type="Build"/>
	</Item>
</Project>
