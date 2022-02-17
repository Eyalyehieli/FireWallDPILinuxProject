package packetsNetFilterDB;

public class FIreWallRulesTable {
	private int id;
	private int activeStatus;
	private ConnectionTable connection;
	private ProtocolTable protocol;
	
	public FIreWallRulesTable(int activeStatus,ProtocolTable protocol,ConnectionTable connection)
	{
		this.activeStatus=activeStatus;
		this.protocol=protocol;
		this.connection=connection;
	}
	public FIreWallRulesTable(String protocolName,int port,String ip)
	{
		this.connection=new ConnectionTable(ip,port);
		this.protocol=new ProtocolTable(protocolName);
	}
	
	public FIreWallRulesTable(int activeStatus) {
		this.activeStatus = activeStatus;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(int activeStatus) {
		this.activeStatus = activeStatus;
	}
	public ConnectionTable getConnection() {
		return connection;
	}
	public void setConnection(ConnectionTable connection) {
		this.connection = connection;
	}
	public ProtocolTable getProtocol() {
		return protocol;
	}
	public void setProtocol(ProtocolTable protocol) {
		this.protocol = protocol;
	}
}
