#ifndef GADGET_NETUTILS_H
#define GADGET_NETUTILS_H

#if defined __WIN32__ || defined WIN32 || defined _Windows || defined _WIN32
#include <winsock2.h>
#include <windows.h>
#else
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#endif


#include <gadget/gadgetConfig.h>
#include <vpr/IO/Socket/SocketStream.h>
#include <vrj/Util/Debug.h>
#include <list>


namespace gadget
{

typedef unsigned short VJ_NETID_TYPE;

const unsigned short MAX_DEVICE_ID = 399;  // allows 400 devices per connection -- allows us to send packet ids and device ids as 2 bytes.
const unsigned short MSG_DEVICE_REQ = 400;
const unsigned short MSG_DEVICE_ACK = 401;
const unsigned short MSG_DEVICE_NACK = 402;
const unsigned short MSG_END_BLOCK = 410;

#ifndef ulong
typedef unsigned long ulong;
#endif
#ifndef ushort
typedef unsigned short ushort;
#endif

// utility function to make sure all bytes are sent in one function call
// Todo: instead of sending all at once -- try putting in a buffer and sending later or in another thread
//    it may (fewer calls to send) or may not (waiting for thread to become active) be faster
inline bool sendAtOnce(vpr::SocketStream& sock_stream, const char* buf, unsigned int bytes_to_send_param){
   vpr::Uint32 total_bytes_sent = 0;
   vpr::Uint32 bytes_just_sent = 0;
   vpr::Uint32 bytes_to_send = bytes_to_send_param;
   while(total_bytes_sent < bytes_to_send){
      sock_stream.send(buf, bytes_to_send - total_bytes_sent, bytes_just_sent);
      total_bytes_sent += bytes_just_sent;
   }
   return 1;
}

// Buffer for transmitting network data.  Important for remote input manager performance since the manager
// needs to send and receive a group of messages once per frame, and the send() function is expensive.
class SendBuffer{
   std::vector<char> mBuffer;
   unsigned int mValidBytes;
public:
   SendBuffer(){
      mValidBytes = 0;
      this->resizeBuffer(512);  // default is just a guess 
   }

   void store(const char* buf, unsigned int bytes_to_send_param){ 
      if (mValidBytes + bytes_to_send_param > mBuffer.size()){
         this->resizeBuffer(mValidBytes + bytes_to_send_param);
      }

      char* array = &(mBuffer[0]);
      for(unsigned int i = 0; i < bytes_to_send_param; i++)
         array[mValidBytes + i] = buf[i];

      mValidBytes += bytes_to_send_param;

      vprDEBUG(vrjDBG_INPUT_MGR, vprDBG_HEX_LVL) << "SendBuffer successfully stored " <<  bytes_to_send_param  << " bytes.  Total bytes: " << mValidBytes << std::endl << vprDEBUG_FLUSH;

   }

   void resizeBuffer(unsigned int new_size){
      vprDEBUG(vrjDBG_INPUT_MGR, vprDBG_HEX_LVL) << "SendBuffer: Resizing mBuffer to: " <<  new_size  << std::endl << vprDEBUG_FLUSH; 
      mBuffer.resize(new_size);
      vprDEBUG(vrjDBG_INPUT_MGR, vprDBG_HEX_LVL) << "SendBuffer: Successful resize: " <<  new_size  << std::endl << vprDEBUG_FLUSH;
   }

   bool sendAllAndClear(vpr::SocketStream& sock_stream){
      sendAtOnce(sock_stream, &(mBuffer[0]), mValidBytes);  // transmit data

      vprDEBUG(vrjDBG_INPUT_MGR, vprDBG_HEX_LVL) << "SendBuffer sent " <<  mValidBytes  << " bytes. " << std::endl << vprDEBUG_FLUSH;

      this->clearBuffer();
      return true;
   }
  
   void clearBuffer(){
      mValidBytes = 0;  // buffer is empty
   }
};

// Given a hostname, use gethostbyname to get Ip address
inline std::string getIpFromHostname(const std::string& hostname){
   
   char* name = new char[hostname.length() + 1];             // create a char* string to be passed to gethostbyname
   strcpy(name, hostname.c_str());
   
   struct hostent* host_info = gethostbyname(name);              // request the host's info
   std::string return_ip = host_info->h_addr_list[0];    // get the main (or first) ip address

   delete[] name;
   return return_ip;                                       // return only the ip address
}

// Endian-safe network to host float
inline float vj_ntohf(float f)
{
  if(vpr::System::isLittleEndian()){
    ulong temp = (ulong) ntohl (*(ulong *)&f);
    return *((float *)&temp);
  }
  else
    return f;
}

// Endian-safe host to network float
inline float vj_htonf(float f)
{
  if(vpr::System::isLittleEndian()){
    ulong temp = (ulong) htonl (*(ulong *)&f);
    return *((float *)&temp);
  }
  else
    return f;
}

// Note the next two functions are (implicitly) limited by the size of integers
inline int binaryToInt(const char* a, const int len){
    int return_int = 0;
    for (int i = 0; i < len; i++) {
        return_int += (int)(a[i]) * (int)(pow((double)256,i));  // that means += a[i] * 256 ^ i
    }
    return return_int;
}


inline unsigned short binaryToUshort(const char* a, const int len){
   // len must be >= 2
   unsigned short return_short = 0;
   ((char*) &return_short)[0] = a[1];
   ((char*) &return_short)[1] = a[0];
   return return_short;
}

inline short binaryToShort(const char* a, const int len){
   // len must be >= 2
   short return_short = 0;
   ((char*) &return_short)[0] = a[1];
   ((char*) &return_short)[1] = a[0];
   return return_short;
}

inline void ushortTo2Bytes(char* dst, const unsigned short in_code){    
    dst[0] = ((char*) &in_code)[1]; // (char)(in_code % 256);
    dst[1] = ((char*) &in_code)[0];  // (char)(in_code / 256);    
}

inline void shortTo2Bytes(char* dst, const short in_code){    
    dst[0] = ((char*) &in_code)[1]; // (char)(in_code % 256);
    dst[1] = ((char*) &in_code)[0];  // (char)(in_code / 256);    
}

template <class T> class IdGenerator{
private:
   T mMAXID;
   T mLargestActiveId;
   std::list<T> mReleasedIds;
public:
   IdGenerator(T max_id) : mMAXID(max_id) {
      mLargestActiveId = 0; // 0 is not used      
   }

   IdGenerator() : mMAXID(255) {
      mLargestActiveId = 0; // 0 is not used      
   }

   T generateNewId(){
      if(mReleasedIds.size() > 0){
         T return_value = mReleasedIds.back();   // get a previously released id
         mReleasedIds.pop_back();             // remote it from list
         // std::cout << "GENERATE ID: " << return_value << std::endl;
         return return_value;                            // and return it
      }
      else if (mLargestActiveId + 1 < mMAXID){
         // std::cout << "GENERATE ID: " << mLargestActiveId + 1 << std::endl;
         return ++mLargestActiveId;      // use the next free id
      }
      else
         // std::cout << "GENERATE ID: None available, returned 0 " << mLargestActiveId + 1 << std::endl;
         return 0;
   }

   void releaseId(int id){
      if (mLargestActiveId == id) {
         mLargestActiveId--;

         std::list<int>::iterator result;
         do {
            result = find(L.begin(), L.end(), mLargestActiveId); 
            if (result != L.end()){                       // if max id has been released
               mReleasedIds.remove(mLargestActiveId);        // remove it from list
               mLargestActiveId--;                           
            }        
         } while (result = L.end());
      }
      else
         mReleasedIds--;
   }
   
   // following function should only be used before ids are generated.
   void setMaxId(T max_id){ mMAXID = max_id; }
};

}  // end namespace gadget

#endif

