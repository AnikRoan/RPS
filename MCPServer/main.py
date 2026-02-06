from app import server

def main():
    server.mcp.run(transport="http", port=8000)



if __name__ == "__main__":
    main()